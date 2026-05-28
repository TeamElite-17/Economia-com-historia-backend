package com.isptec.economiahistoriaapi;

import com.isptec.economiahistoriaapi.controller.PostController;
import com.isptec.economiahistoriaapi.dto.PostDTO;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class TestRunner implements CommandLineRunner {

    private final PostController postController;

    public TestRunner(PostController postController) {
        this.postController = postController;
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            System.out.println("=== RUNNING TEST RUNNER ===");
            PostDTO dto = new PostDTO();
            dto.setThreadId("14470058-39c7-4eac-b1a9-3310c34a8975");
            dto.setUserId("cf5a4205-1823-448f-9a99-5eafe516ec50"); // Assuming there is a user, or we can use null and let it fail
            dto.setContent("Test content via runner");
            System.out.println("Result: " + postController.createPost(dto));
        } catch (Exception e) {
            System.err.println("TEST RUNNER ERROR:");
            e.printStackTrace();
        }
    }
}
