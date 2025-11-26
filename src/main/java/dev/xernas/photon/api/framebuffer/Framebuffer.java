package dev.xernas.photon.api.framebuffer;

import java.util.ArrayList;
import java.util.List;

public class Framebuffer {

    private final List<FramebufferAttachment> attachments = new ArrayList<>();

    public Framebuffer(FramebufferAttachment... attachments) {
        this.attachments.addAll(List.of(attachments));
    }

    public List<FramebufferAttachment> getAttachments() {
        return attachments;
    }

}
