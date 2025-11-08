package dev.xernas.photon.vulkan;

import dev.xernas.photon.PhotonAPI;
import dev.xernas.photon.api.PhotonLogic;
import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.exceptions.VulkanException;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWVulkan;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.*;

import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.HashSet;
import java.util.Set;

public class VulkanInstance implements PhotonLogic {

    private static final Set<String> VALIDATION_LAYERS = new HashSet<>();

    private final boolean enableValidationLayers;

    private VkInstance instance;
    private long debugMessenger;

    public VulkanInstance(boolean enableValidationLayers) {
        this.enableValidationLayers = enableValidationLayers;
        if(enableValidationLayers) VALIDATION_LAYERS.add("VK_LAYER_KHRONOS_validation");
    }

    @Override
    public void start() throws PhotonException {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            // Application Info
            VkApplicationInfo appInfo = VkApplicationInfo.calloc(stack);
            appInfo.sType(VK10.VK_STRUCTURE_TYPE_APPLICATION_INFO);
            appInfo.pApplicationName(stack.UTF8(PhotonAPI.getAppName()));
            String[] appVersionParts = PhotonAPI.getAppVersion().split("\\.");
            int major = Integer.parseInt(appVersionParts[0]);
            int minor = Integer.parseInt(appVersionParts[1]);
            if (appVersionParts.length > 2) appInfo.applicationVersion(VK10.VK_MAKE_VERSION(major, minor, Integer.parseInt(appVersionParts[2])));
            else appInfo.applicationVersion(VK10.VK_MAKE_VERSION(major, minor, 0));
            appInfo.pEngineName(stack.UTF8(PhotonAPI.getEngineName()));
            String[] engineVersionParts = PhotonAPI.getEngineVersion().split("\\.");
            major = Integer.parseInt(engineVersionParts[0]);
            minor = Integer.parseInt(engineVersionParts[1]);
            if (engineVersionParts.length > 2) appInfo.engineVersion(VK10.VK_MAKE_VERSION(major, minor, Integer.parseInt(engineVersionParts[2])));
            else appInfo.engineVersion(VK10.VK_MAKE_VERSION(major, minor, 0));
            String[] apiVersionParts = PhotonAPI.getLibrary().getVersion().split("\\.");
            major = Integer.parseInt(apiVersionParts[0]);
            minor = Integer.parseInt(apiVersionParts[1]);
            appInfo.apiVersion(VK10.VK_MAKE_VERSION(major, minor, 0));

            // Validation Layers
            if (enableValidationLayers && !checkValidationLayerSupport()) throw new VulkanException("Validation layers requested, but not available!");
            PointerBuffer ppEnabledLayerNames = null;
            if (enableValidationLayers) {
                ppEnabledLayerNames = stack.mallocPointer(VALIDATION_LAYERS.size());
                for (String layerName : VALIDATION_LAYERS) ppEnabledLayerNames.put(stack.UTF8(layerName));
                ppEnabledLayerNames.flip();
            }
            VkDebugUtilsMessengerCreateInfoEXT debugCreateInfo = VkDebugUtilsMessengerCreateInfoEXT.calloc(stack)
                    .sType(EXTDebugUtils.VK_STRUCTURE_TYPE_DEBUG_UTILS_MESSENGER_CREATE_INFO_EXT)
                    .messageSeverity(
                            EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_SEVERITY_VERBOSE_BIT_EXT |
                                    EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_SEVERITY_WARNING_BIT_EXT |
                                    EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_SEVERITY_ERROR_BIT_EXT
                    )
                    .messageType(
                            EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_TYPE_GENERAL_BIT_EXT |
                                    EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_TYPE_VALIDATION_BIT_EXT |
                                    EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_TYPE_PERFORMANCE_BIT_EXT
                    )
                    .pfnUserCallback((messageSeverity, messageTypes, pCallbackData, pUserData) -> {
                        VkDebugUtilsMessengerCallbackDataEXT callbackData = VkDebugUtilsMessengerCallbackDataEXT.create(pCallbackData);
                        System.err.println(callbackData.pMessageString());
                        return VK10.VK_FALSE;
                    });

            // Instance Create Info
            PointerBuffer requiredExtensions = GLFWVulkan.glfwGetRequiredInstanceExtensions();
            VkInstanceCreateInfo createInfo = VkInstanceCreateInfo.calloc(stack);
            createInfo.sType(VK10.VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO);
            createInfo.pApplicationInfo(appInfo);
            if (enableValidationLayers) {
                PointerBuffer extensions = stack.mallocPointer(requiredExtensions.capacity() + 1);
                extensions.put(requiredExtensions);
                extensions.put(stack.UTF8(EXTDebugUtils.VK_EXT_DEBUG_UTILS_EXTENSION_NAME));
                extensions.flip();
                createInfo.ppEnabledExtensionNames(extensions);
                createInfo.ppEnabledLayerNames(ppEnabledLayerNames);
                createInfo.pNext(debugCreateInfo.address());
            }
            else createInfo.ppEnabledExtensionNames(requiredExtensions);

            // Create Instance
            PointerBuffer pInstance = stack.mallocPointer(1);
            int err = VK10.vkCreateInstance(createInfo, null, pInstance);
            if (err != VK10.VK_SUCCESS) throw new VulkanException("Failed to create Vulkan instance: " + VulkanErrorHelper.vkResultToString(err));
            long instanceHandle = pInstance.get(0);
            instance = new VkInstance(instanceHandle, createInfo);

            if (enableValidationLayers) setupDebugMessenger(debugCreateInfo, stack);
        }
    }

    @Override
    public void dispose() throws PhotonException {
        if (enableValidationLayers) EXTDebugUtils.vkDestroyDebugUtilsMessengerEXT(instance, debugMessenger, null);
        VK10.vkDestroyInstance(instance, null);
    }

    public VkInstance getInstance() {
        return instance;
    }

    public long getDebugMessenger() {
        return debugMessenger;
    }

    private void setupDebugMessenger(VkDebugUtilsMessengerCreateInfoEXT createInfo, MemoryStack stack) throws VulkanException {
        LongBuffer pDebugMessenger = stack.longs(VK10.VK_NULL_HANDLE);
        if (EXTDebugUtils.vkCreateDebugUtilsMessengerEXT(instance, createInfo, null, pDebugMessenger) != VK10.VK_SUCCESS) throw new VulkanException("Failed to set up debug messenger");
        debugMessenger = pDebugMessenger.get(0);
    }

    private boolean checkValidationLayerSupport() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer layerCount = stack.ints(0);
            VK10.vkEnumerateInstanceLayerProperties(layerCount, null);

            VkLayerProperties.Buffer availableLayers = VkLayerProperties.malloc(layerCount.get(0), stack);
            VK10.vkEnumerateInstanceLayerProperties(layerCount, availableLayers);

            for (VkLayerProperties layerProperties : availableLayers) {
                if (VALIDATION_LAYERS.contains(layerProperties.layerNameString())) {
                    return true;
                }
            }
        }
        return false;
    }

}
