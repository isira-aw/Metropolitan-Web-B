package com.metropolitan.backend.service;

import com.metropolitan.backend.dto.PageResponse;
import com.metropolitan.backend.model.News;
import com.metropolitan.backend.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsRepository newsRepository;

    public PageResponse<News> getNews(int page, int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.DESC, "date"));

        Page<News> newsPage = newsRepository.findAll(pageable);
        long total = newsRepository.count();

        return PageResponse.of(newsPage.getContent(), total, page, limit);
    }

    public PageResponse<News> getNewsWithFilters(
            LocalDateTime fromDate,
            LocalDateTime toDate,
            int page,
            int limit
    ) {
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.DESC, "date"));

        Page<News> newsPage = newsRepository.findWithFilters(fromDate, toDate, pageable);
        long total = newsRepository.countWithFilters(fromDate, toDate);

        return PageResponse.of(newsPage.getContent(), total, page, limit);
    }

    public Optional<News> getNewsItem(Long id) {
        return newsRepository.findById(id);
    }

    public News createNews(News news) {
        return newsRepository.save(news);
    }

    public News updateNews(Long id, News newsDetails) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("News not found with id: " + id));

        news.setTitle(newsDetails.getTitle());
        news.setContent(newsDetails.getContent());
        news.setSummary(newsDetails.getSummary());
        news.setImage(newsDetails.getImage());
        news.setDate(newsDetails.getDate());

        return newsRepository.save(news);
    }

    public void deleteNews(Long id) {
        if (!newsRepository.existsById(id)) {
            throw new RuntimeException("News not found with id: " + id);
        }
        newsRepository.deleteById(id);
    }
}
