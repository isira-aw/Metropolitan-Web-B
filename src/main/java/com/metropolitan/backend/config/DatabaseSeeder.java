package com.metropolitan.backend.config;

import com.metropolitan.backend.model.*;
import com.metropolitan.backend.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final CaseStudyService caseStudyService;
    private final NewsService newsService;
    private final TestimonialService testimonialService;

    private static final String[] DIVISIONS = {
        "Central AC",
        "Elevators and Travelators",
        "Fire Detection & Protection",
        "Generator",
        "Solar",
        "ELV"
    };

    private static final String[] SAMPLE_IMAGES = {
        "https://images.unsplash.com/photo-1558444479-c8498174f680?auto=format&fit=crop&q=80&w=2070",
        "https://images.unsplash.com/photo-1516733968668-dbdce39c46ef?auto=format&fit=crop&q=80&w=2070",
        "https://images.unsplash.com/photo-1581094288338-2314dddb7ec4?auto=format&fit=crop&q=80&w=2070",
        "https://images.unsplash.com/photo-1544724569-5f546fd6f2b5?auto=format&fit=crop&q=80&w=2070",
        "https://images.unsplash.com/photo-1509391366360-fe5bb6585828?auto=format&fit=crop&q=80&w=2070",
        "https://images.unsplash.com/photo-1550751827-4bd374c3f58b?auto=format&fit=crop&q=80&w=2070"
    };

    @Override
    public void run(String... args) {
        // Check if database is already seeded
        var existingCaseStudies = caseStudyService.getCaseStudies(null, 1, 1);
        if (existingCaseStudies.getTotal() > 0) {
            System.out.println("Database already seeded. Skipping seed operation.");
            return;
        }

        System.out.println("Seeding database...");

        // Seed Case Studies
        for (int i = 0; i < 18; i++) {
            String division = DIVISIONS[i % DIVISIONS.length];
            CaseStudy caseStudy = new CaseStudy();
            caseStudy.setTitle(division + " Project " + (i + 1) + ": Modern Solution");
            caseStudy.setDescription("A groundbreaking project delivering state-of-the-art infrastructure and sustainable design. This project exemplifies our commitment to excellence and innovation in the metropolitan landscape.");
            caseStudy.setImage(SAMPLE_IMAGES[i % SAMPLE_IMAGES.length]);
            caseStudy.setDivision(division);
            caseStudy.setClient("Client " + (i + 1) + " Corp");
            caseStudy.setLocation("Metropolitan Area");
            caseStudy.setCompletionDate("2024");
            caseStudyService.createCaseStudy(caseStudy);
        }

        // Seed News
        for (int i = 0; i < 10; i++) {
            News news = new News();
            news.setTitle("Metropolitan News: Expansion into " + DIVISIONS[i % DIVISIONS.length] + " Sector");
            news.setContent("<p>We are expanding our operations to provide even better services in the metropolitan region.</p>");
            news.setImage(SAMPLE_IMAGES[i % SAMPLE_IMAGES.length]);
            news.setSummary("We are thrilled to announce a significant achievement in our ongoing efforts to redefine urban living.");
            newsService.createNews(news);
        }

        // Seed Testimonials
        String[][] testimonialData = {
            {"John Smith", "Project Manager", "Exceptional quality and reliability."},
            {"Sarah Jane", "Facility Director", "Professional team and great support."}
        };

        for (String division : DIVISIONS) {
            for (String[] data : testimonialData) {
                Testimonial testimonial = new Testimonial();
                testimonial.setAuthor(data[0]);
                testimonial.setRole(data[1]);
                testimonial.setContent(data[2]);
                testimonial.setDivision(division);
                testimonialService.createTestimonial(testimonial);
            }
        }

        System.out.println("Database seeded successfully.");
    }
}
