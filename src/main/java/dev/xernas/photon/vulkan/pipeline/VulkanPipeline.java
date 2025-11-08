package dev.xernas.photon.vulkan.pipeline;

import dev.xernas.photon.api.PhotonLogic;
import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.exceptions.VulkanException;
import dev.xernas.photon.vulkan.device.VulkanDevice;
import dev.xernas.photon.vulkan.swapchain.VulkanRenderPass;
import dev.xernas.photon.vulkan.swapchain.VulkanSwapChain;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.List;

public class VulkanPipeline implements PhotonLogic {

    private final List<Integer> dynamicStates = List.of(
            VK10.VK_DYNAMIC_STATE_VIEWPORT,
            VK10.VK_DYNAMIC_STATE_SCISSOR
    );

    private final VulkanShader shader;
    private final VulkanRenderPass renderPass;
    private final VulkanSwapChain swapChain;
    private final VulkanDevice device;

    private long pipelineLayout;
    private VkPipelineShaderStageCreateInfo.Buffer shaderStages;
    private VkPipelineDynamicStateCreateInfo dynamicState;
    private VkPipelineVertexInputStateCreateInfo vertexInputInfo;
    private VkPipelineInputAssemblyStateCreateInfo inputAssembly;
    private VkPipelineViewportStateCreateInfo viewportState;
    private VkPipelineRasterizationStateCreateInfo rasterizationState;
    private VkPipelineMultisampleStateCreateInfo multisampling;
    private VkPipelineColorBlendStateCreateInfo colorBlending;

    private long graphicsPipeline;

    public VulkanPipeline(VulkanShader shader, VulkanRenderPass renderPass, VulkanSwapChain swapChain, VulkanDevice device) {
        this.shader = shader;
        this.renderPass = renderPass;
        this.swapChain = swapChain;
        this.device = device;
    }

    @Override
    public void start() throws PhotonException {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            setupShaderStages(stack);
            setupFixedFunctionStages(stack);

            VkGraphicsPipelineCreateInfo.Buffer createInfo = VkGraphicsPipelineCreateInfo.calloc(1, stack)
                    .sType(VK10.VK_STRUCTURE_TYPE_GRAPHICS_PIPELINE_CREATE_INFO)
                    .pStages(shaderStages)
                    .pVertexInputState(vertexInputInfo)
                    .pInputAssemblyState(inputAssembly)
                    .pViewportState(viewportState)
                    .pRasterizationState(rasterizationState)
                    .pMultisampleState(multisampling)
                    .pColorBlendState(colorBlending)
                    .pDynamicState(dynamicState)
                    .pDepthStencilState(null)
                    .layout(pipelineLayout);
            createInfo.renderPass(renderPass.getRenderPass());
            createInfo.subpass(0);
            createInfo.basePipelineHandle(VK10.VK_NULL_HANDLE);
            createInfo.basePipelineIndex(-1);

            LongBuffer pGraphicsPipeline = stack.mallocLong(1);
            if (VK10.vkCreateGraphicsPipelines(device.getDevice(), VK10.VK_NULL_HANDLE, createInfo, null, pGraphicsPipeline) != VK10.VK_SUCCESS) throw new VulkanException("Failed to create graphics pipeline");
            graphicsPipeline = pGraphicsPipeline.get(0);
        }
    }

    @Override
    public void dispose() throws PhotonException {
        shader.dispose();
        VK10.vkDestroyPipeline(device.getDevice(), graphicsPipeline, null);
        VK10.vkDestroyPipelineLayout(device.getDevice(), pipelineLayout, null);
    }

    public long getGraphicsPipeline() {
        return graphicsPipeline;
    }

    private void setupShaderStages(MemoryStack stack) throws PhotonException {
        shader.start();
        VkPipelineShaderStageCreateInfo vertexShaderStageInfo = VkPipelineShaderStageCreateInfo.calloc(stack)
                .sType(VK10.VK_STRUCTURE_TYPE_PIPELINE_SHADER_STAGE_CREATE_INFO)
                .stage(VK10.VK_SHADER_STAGE_VERTEX_BIT)
                .module(((VulkanShaderModule) shader.getVertexShaderModule()).getShaderModule())
                .pName(stack.UTF8("main"));
        VkPipelineShaderStageCreateInfo fragmentShaderStageInfo = VkPipelineShaderStageCreateInfo.calloc(stack)
                .sType(VK10.VK_STRUCTURE_TYPE_PIPELINE_SHADER_STAGE_CREATE_INFO)
                .stage(VK10.VK_SHADER_STAGE_FRAGMENT_BIT)
                .module(((VulkanShaderModule) shader.getFragmentShaderModule()).getShaderModule())
                .pName(stack.UTF8("main"));
        shaderStages = VkPipelineShaderStageCreateInfo.calloc(2, stack)
                .put(0, vertexShaderStageInfo)
                .put(1, fragmentShaderStageInfo);
    }

    private void setupFixedFunctionStages(MemoryStack stack) throws VulkanException {
        dynamicState = VkPipelineDynamicStateCreateInfo.calloc(stack)
                .sType(VK10.VK_STRUCTURE_TYPE_PIPELINE_DYNAMIC_STATE_CREATE_INFO)
                .pDynamicStates(stack.ints(dynamicStates.stream().mapToInt(i -> i).toArray()));

        vertexInputInfo = VkPipelineVertexInputStateCreateInfo.calloc(stack)
                .sType(VK10.VK_STRUCTURE_TYPE_PIPELINE_VERTEX_INPUT_STATE_CREATE_INFO)
                .pVertexBindingDescriptions(null)
                .pVertexAttributeDescriptions(null);

        inputAssembly = VkPipelineInputAssemblyStateCreateInfo.calloc(stack)
                .sType(VK10.VK_STRUCTURE_TYPE_PIPELINE_INPUT_ASSEMBLY_STATE_CREATE_INFO)
                .topology(VK10.VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST)
                .primitiveRestartEnable(false);

        VkViewport viewport = VkViewport.calloc(stack)
                .x(0.0f)
                .y(0.0f)
                .width(swapChain.getExtentWidth())
                .height(swapChain.getExtentHeight())
                .minDepth(0.0f)
                .maxDepth(1.0f);

        VkRect2D scissor = VkRect2D.calloc(stack)
                .offset(VkOffset2D.calloc(stack).set(0, 0))
                .extent(swapChain.getExtent(stack));

        viewportState = VkPipelineViewportStateCreateInfo.calloc(stack)
                .sType(VK10.VK_STRUCTURE_TYPE_PIPELINE_VIEWPORT_STATE_CREATE_INFO)
                .pViewports(VkViewport.calloc(1, stack).put(0, viewport))
                .pScissors(VkRect2D.calloc(1, stack).put(0, scissor));

        rasterizationState = VkPipelineRasterizationStateCreateInfo.calloc(stack)
                .sType(VK10.VK_STRUCTURE_TYPE_PIPELINE_RASTERIZATION_STATE_CREATE_INFO)
                .depthClampEnable(false)
                .rasterizerDiscardEnable(false)
                .polygonMode(VK10.VK_POLYGON_MODE_FILL)
                .lineWidth(1.0f)
                .cullMode(VK10.VK_CULL_MODE_BACK_BIT)
                .frontFace(VK10.VK_FRONT_FACE_CLOCKWISE)
                .depthBiasEnable(false)
                .depthBiasConstantFactor(0.0f)
                .depthBiasClamp(0.0f)
                .depthBiasSlopeFactor(0.0f);

        multisampling = VkPipelineMultisampleStateCreateInfo.calloc(stack)
                .sType(VK10.VK_STRUCTURE_TYPE_PIPELINE_MULTISAMPLE_STATE_CREATE_INFO)
                .sampleShadingEnable(false)
                .rasterizationSamples(VK10.VK_SAMPLE_COUNT_1_BIT)
                .minSampleShading(1.0f)
                .pSampleMask(null)
                .alphaToCoverageEnable(false)
                .alphaToOneEnable(false);

        VkPipelineColorBlendAttachmentState colorBlendAttachment = VkPipelineColorBlendAttachmentState.calloc(stack)
                .colorWriteMask(VK10.VK_COLOR_COMPONENT_R_BIT | VK10.VK_COLOR_COMPONENT_G_BIT | VK10.VK_COLOR_COMPONENT_B_BIT | VK10.VK_COLOR_COMPONENT_A_BIT)
                .blendEnable(false)
                .srcColorBlendFactor(VK10.VK_BLEND_FACTOR_ONE)
                .dstColorBlendFactor(VK10.VK_BLEND_FACTOR_ZERO)
                .colorBlendOp(VK10.VK_BLEND_OP_ADD)
                .srcAlphaBlendFactor(VK10.VK_BLEND_FACTOR_ONE)
                .dstAlphaBlendFactor(VK10.VK_BLEND_FACTOR_ZERO)
                .alphaBlendOp(VK10.VK_BLEND_OP_ADD);

        colorBlending = VkPipelineColorBlendStateCreateInfo.calloc(stack)
                .sType(VK10.VK_STRUCTURE_TYPE_PIPELINE_COLOR_BLEND_STATE_CREATE_INFO)
                .logicOpEnable(false)
                .logicOp(VK10.VK_LOGIC_OP_COPY)
                .pAttachments(VkPipelineColorBlendAttachmentState.calloc(1, stack).put(0, colorBlendAttachment))
                .blendConstants(stack.floats(0.0f, 0.0f, 0.0f, 0.0f));

        // Pipeline layout
        VkPipelineLayoutCreateInfo pipelineLayoutInfo = VkPipelineLayoutCreateInfo.calloc(stack)
                .sType(VK10.VK_STRUCTURE_TYPE_PIPELINE_LAYOUT_CREATE_INFO)
                .pSetLayouts(null)
                .pPushConstantRanges(null);

        LongBuffer pPipelineLayout = stack.mallocLong(1);
        if (VK10.vkCreatePipelineLayout(device.getDevice(), pipelineLayoutInfo, null, pPipelineLayout) != VK10.VK_SUCCESS) throw new VulkanException("Failed to create pipeline layout");
        pipelineLayout = pPipelineLayout.get(0);
    }
}
