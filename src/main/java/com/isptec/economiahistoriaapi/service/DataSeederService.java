package com.isptec.economiahistoriaapi.service;

import com.isptec.economiahistoriaapi.enums.*;
import com.isptec.economiahistoriaapi.model.*;
import com.isptec.economiahistoriaapi.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataSeederService implements ApplicationRunner {

        private final UserRepository userRepository;
        private final CategoryRepository categoryRepository;
        private final ContentItemRepository contentItemRepository;
        private final QuizRepository quizRepository;
        private final QuestionRepository questionRepository;
        private final AnswerOptionRepository answerOptionRepository;
        private final ForumThreadService forumThreadService;
        private final PostService postService;
        private final PasswordEncoder passwordEncoder;

        @Override
        @Transactional
        public void run(ApplicationArguments args) {
                try {
                        seedAdminUser();
                        seedTestUsers();
                        seedCategories();
                        seedContentItems();
                        seedQuizzes();
                        seedForumData();
                } catch (Exception e) {
                        log.warn("Seeder warning (dados já existem ou erro menor): {}", e.getMessage());
                }
        }

        private User seedAdminUser() {
                return userRepository.findByEmail("admin@economia.ao").orElseGet(() -> {
                        User admin = User.builder()
                                        .name("Administrador")
                                        .email("admin@economia.ao")
                                        .passwordHash(passwordEncoder.encode("Admin@1234"))
                                        .role(UserRole.SUPERADMIN)
                                        .preferredLanguage("pt")
                                        .build();
                        User saved = userRepository.save(admin);
                        log.info("✅ Admin criado: admin@economia.ao / Admin@1234");
                        return saved;
                });
        }

        private void seedTestUsers() {
                Object[][] accounts = {
                                { "Prof. João Escritor", "escritor@economia.ao", "Escritor@1234", UserRole.ESCRITOR },
                                { "Maria Revisora", "revisor@economia.ao", "Revisor@1234", UserRole.REVISOR },
                                { "Carlos Aprovador", "aprovador@economia.ao", "Aprovador@1234", UserRole.APROVADOR },
                                { "Ana Estudante", "estudante@economia.ao", "Estudante@1234", UserRole.ESTUDANTE },
                                { "Pedro Administrador", "gestor@economia.ao", "Gestor@1234", UserRole.ADMIN },
                };
                for (Object[] account : accounts) {
                        String email = (String) account[1];
                        UserRole expectedRole = (UserRole) account[3];
                        userRepository.findByEmail(email).ifPresentOrElse(
                                existingUser -> {
                                        // Corrige o role se estiver errado (ex: criado antes como ESTUDANTE)
                                        if (existingUser.getRole() != expectedRole) {
                                                existingUser.setRole(expectedRole);
                                                userRepository.save(existingUser);
                                                log.info("🔧 Role corrigido: {} → {} ({})", email, expectedRole, existingUser.getUserId());
                                        }
                                },
                                () -> {
                                        User user = User.builder()
                                                        .name((String) account[0])
                                                        .email(email)
                                                        .passwordHash(passwordEncoder.encode((String) account[2]))
                                                        .role(expectedRole)
                                                        .preferredLanguage("pt")
                                                        .build();
                                        userRepository.save(user);
                                        log.info("✅ Utilizador de teste: {} / {} ({})", email, account[2], expectedRole);
                                }
                        );
                }
        }


        private void seedCategories() {
                String[][] cats = {
                                { "Historia de Angola", "historia-angola", "Factos e eventos historicos de Angola" },
                                { "Economia Colonial", "economia-colonial",
                                                "Conteudos sobre o periodo colonial em Angola" },
                                { "Financas Pessoais", "financas-pessoais",
                                                "Gestao de dinheiro e financas para o dia a dia" },
                                { "Economia Global", "economia-global",
                                                "Economia mundial e suas influencias em Angola" },
                                { "Mercados Financeiros", "mercados-financeiros", "Bolsas, investimentos e mercados" },
                                { "Empreendedorismo", "empreendedorismo", "Como criar e gerir negocios em Angola" }
                };
                for (String[] c : cats) {
                        if (categoryRepository.findByName(c[0]).isEmpty()) {
                                categoryRepository.save(Category.builder()
                                                .name(c[0]).slug(c[1]).description(c[2]).build());
                        }
                }
                log.info("✅ Categorias verificadas/criadas");
        }

        private void seedContentItems() {
                if (contentItemRepository.count() > 3)
                        return;

                Category hist = categoryRepository.findByName("Historia de Angola").orElse(null);
                Category econ = categoryRepository.findByName("Economia Colonial").orElse(null);
                Category fin = categoryRepository.findByName("Financas Pessoais").orElse(null);
                Category global = categoryRepository.findByName("Economia Global").orElse(null);

                // Obter o escritor principal para atribuir como autor dos conteúdos
                String escritorId = userRepository.findByEmail("escritor@economia.ao")
                        .map(User::getUserId).orElse(null);

                List<ContentItem> items = new ArrayList<>();

                items.add(ContentItem.builder()
                                .title("A Rota do Ouro no Seculo XVIII")
                                .description("Video sobre o fluxo de ouro e minerios nas rotas comerciais do periodo colonial.")
                                .mediaType(MediaType.VIDEO)
                                .sourceUrl("https://www.youtube.com/watch?v=dQw4w9WgXcQ")
                                .thumbnailUrl("https://images.unsplash.com/photo-1599707367072-cd6ada2bc375?w=600")
                                .durationSeconds(1200)
                                .status(ContentStatus.PUBLISHED)
                                .publishedAt(new Date())
                                .authorId(escritorId)
                                .categories(econ != null ? List.of(econ) : new ArrayList<>())
                                .build());

                items.add(ContentItem.builder()
                                .title("Independencia de Angola: Contexto Economico")
                                .description("Analise das condicoes economicas que levaram a independencia de Angola em 1975.")
                                .mediaType(MediaType.TEXT)
                                .thumbnailUrl("https://images.unsplash.com/photo-1489493887464-892be6d1daae?w=600")
                                .wordCount(2500)
                                .status(ContentStatus.PUBLISHED)
                                .publishedAt(new Date())
                                .authorId(escritorId)
                                .categories(hist != null ? List.of(hist) : new ArrayList<>())
                                .build());

                items.add(ContentItem.builder()
                                .title("Podcast: O Kwanza e a Economia Angolana")
                                .description("Entrevista com economistas sobre a historia do Kwanza e perspectivas futuras.")
                                .mediaType(MediaType.PODCAST)
                                .thumbnailUrl("https://images.unsplash.com/photo-1478737270239-2f02b77fc618?w=600")
                                .durationSeconds(2700)
                                .status(ContentStatus.PUBLISHED)
                                .publishedAt(new Date())
                                .authorId(escritorId)
                                .categories(fin != null ? List.of(fin) : new ArrayList<>())
                                .build());

                items.add(ContentItem.builder()
                                .title("Gestao de Financas Pessoais em Angola")
                                .description("Guia pratico para gerir as suas financas pessoais num contexto de inflacao.")
                                .mediaType(MediaType.TEXT)
                                .thumbnailUrl("https://images.unsplash.com/photo-1579621970563-ebec7560ff3e?w=600")
                                .wordCount(3200)
                                .status(ContentStatus.PUBLISHED)
                                .publishedAt(new Date())
                                .authorId(escritorId)
                                .categories(fin != null ? List.of(fin) : new ArrayList<>())
                                .build());

                items.add(ContentItem.builder()
                                .title("O Petroleo e o Desenvolvimento de Angola")
                                .description("O papel do petroleo no desenvolvimento economico de Angola desde os anos 70.")
                                .mediaType(MediaType.VIDEO)
                                .sourceUrl("https://www.youtube.com/watch?v=dQw4w9WgXcQ")
                                .thumbnailUrl("https://images.unsplash.com/photo-1582562124811-c09040d0a901?w=600")
                                .durationSeconds(1800)
                                .status(ContentStatus.PUBLISHED)
                                .publishedAt(new Date())
                                .authorId(escritorId)
                                .categories(global != null ? List.of(global) : new ArrayList<>())
                                .build());

                items.add(ContentItem.builder()
                                .title("Mercado de Capitais em Angola: Oportunidades")
                                .description("Como funciona o mercado de capitais angolano e como investir de forma segura.")
                                .mediaType(MediaType.TEXT)
                                .thumbnailUrl("https://images.unsplash.com/photo-1611974789855-9c2a0a7236a3?w=600")
                                .wordCount(2800)
                                .status(ContentStatus.PUBLISHED)
                                .publishedAt(new Date())
                                .authorId(escritorId)
                                .categories(fin != null ? List.of(fin) : new ArrayList<>())
                                .build());

                contentItemRepository.saveAll(items);
                log.info("✅ {} conteudos criados", items.size());
        }

        private void seedQuizzes() {
                if (quizRepository.count() > 0)
                        return;

                // Quiz 1
                Quiz quiz1 = quizRepository.save(Quiz.builder()
                                .title("Historia Economica de Angola")
                                .description("Teste os seus conhecimentos sobre a historia economica de Angola.")
                                .passingScore(60)
                                .status(ContentStatus.PUBLISHED)
                                .build());

                createQuestion(quiz1, "Em que ano Angola conquistou a sua independencia?",
                                new String[] { "1975", "1961", "1980", "1970" }, 0);
                createQuestion(quiz1, "Qual e a moeda oficial de Angola?",
                                new String[] { "Kwanza", "Escudo", "Rand", "Dolar" }, 0);
                createQuestion(quiz1, "Angola e um dos maiores produtores mundiais de que recurso?",
                                new String[] { "Petroleo", "Cobre", "Ouro", "Gas Natural" }, 0);
                createQuestion(quiz1, "Qual e a capital de Angola?",
                                new String[] { "Luanda", "Huambo", "Benguela", "Lubango" }, 0);

                // Quiz 2
                Quiz quiz2 = quizRepository.save(Quiz.builder()
                                .title("Financas Pessoais Basicas")
                                .description("Avalie os seus conhecimentos sobre gestao de financas pessoais.")
                                .passingScore(70)
                                .status(ContentStatus.PUBLISHED)
                                .build());

                createQuestion(quiz2, "O que e uma taxa de inflacao?",
                                new String[] {
                                                "O aumento geral dos precos ao longo do tempo",
                                                "A taxa de juro de um emprestimo",
                                                "A taxa de cambio entre moedas",
                                                "O crescimento do PIB"
                                }, 0);
                createQuestion(quiz2, "O que significa PIB?",
                                new String[] {
                                                "Produto Interno Bruto",
                                                "Producao Industrial Bruta",
                                                "Produto Internacional Bancario",
                                                "Plano de Investimento Basico"
                                }, 0);
                createQuestion(quiz2, "O que e uma carteira de investimentos diversificada?",
                                new String[] {
                                                "Varios tipos de activos para reduzir o risco",
                                                "Uma conta com varios bancos",
                                                "Um seguro de vida",
                                                "Um emprestimo bancario"
                                }, 0);

                log.info("✅ Quizzes criados com perguntas");
        }

        private void createQuestion(Quiz quiz, String text, String[] options, int correctIndex) {
                Question q = questionRepository.save(Question.builder()
                                .text(text)
                                .type(QuestionType.SINGLE_CHOICE)
                                .points(10)
                                .quiz(quiz)
                                .build());

                List<AnswerOption> answers = new ArrayList<>();
                for (int i = 0; i < options.length; i++) {
                        answers.add(AnswerOption.builder()
                                        .text(options[i])
                                        .correct(i == correctIndex)
                                        .question(q)
                                        .build());
                }
                answerOptionRepository.saveAll(answers);
        }

        private void seedForumData() {
                if (!forumThreadService.getAllThreads().isEmpty())
                        return;

                User admin = userRepository.findByEmail("admin@economia.ao").orElse(null);

                ForumThread t1 = forumThreadService.createThread(
                                ForumThread.builder().title("Como poupar dinheiro em Angola com a inflacao atual?")
                                                .build());

                ForumThread t2 = forumThreadService.createThread(
                                ForumThread.builder().title("O futuro do Kwanza face ao Dolar").build());

                ForumThread t3 = forumThreadService.createThread(
                                ForumThread.builder().title("Melhores investimentos para jovens angolanos").build());

                if (admin != null) {
                        postService.createPost(Post.builder()
                                        .content("A inflacao em Angola tem sido um grande desafio. Partilhem as vossas estrategias para poupar dinheiro e proteger o vosso poder de compra!")
                                        .forumThread(t1)
                                        .author(admin)
                                        .build());

                        postService.createPost(Post.builder()
                                        .content("O Kwanza continua a desvalorizar face ao dolar. Que medidas o governo deveria tomar para estabilizar a moeda nacional?")
                                        .forumThread(t2)
                                        .author(admin)
                                        .build());

                        postService.createPost(Post.builder()
                                        .content("Para jovens angolanos, quais sao as melhores opcoes de investimento? Depositos a prazo, acoes, imoveis ou cripto?")
                                        .forumThread(t3)
                                        .author(admin)
                                        .build());
                }

                log.info("✅ Forum data criado ({} threads)", 3);
        }
}
