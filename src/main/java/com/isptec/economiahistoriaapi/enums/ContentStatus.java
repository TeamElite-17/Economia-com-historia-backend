package com.isptec.economiahistoriaapi.enums;

/**
 * Representa os estados do fluxo editorial de conteúdos e quizzes.
 * DRAFT → UNDER_REVIEW → APPROVED → PUBLISHED
 *                      ↘ REJECTED
 */
public enum ContentStatus {
    DRAFT,        // Rascunho criado pelo Escritor
    UNDER_REVIEW, // Submetido para revisão pelo Revisor
    APPROVED,     // Aprovado pelo Aprovador, pronto a publicar
    PUBLISHED,    // Publicado e visível para todos
    REJECTED      // Rejeitado, pode ser editado e resubmetido
}
