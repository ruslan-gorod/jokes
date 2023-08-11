package com.example.jokes.controller;

import com.example.jokes.model.Joke;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
class JokeController {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${joke.api.url}")
    private String url;

    @Value("${joke.api.batchSize}")
    private int batchSize = 10;
    @Value("${joke.api.defaultCount}")
    private int DEFAULT_COUNT = 100;

    @GetMapping("/jokes")
    public List<Joke> getJokes(@RequestParam(defaultValue = "5") int count) throws InterruptedException, ExecutionException {
        if (count > DEFAULT_COUNT) {
            throw new IllegalArgumentException("At one time you can get no more than 100 jokes.");
        }

        int numBatches = (int) Math.ceil((double) count / batchSize);

        List<CompletableFuture<List<Joke>>> batchFutures = new ArrayList<>();

        IntStream.range(0, numBatches)
                .map(batchIndex -> batchIndex * batchSize)
                .forEach(startIdx -> {
                    int endIdx = Math.min(startIdx + batchSize, count);
                    batchFutures.add(fetchJokesAsync(endIdx - startIdx));
                });

        List<Joke> jokes = new ArrayList<>();

        for (CompletableFuture<List<Joke>> future : batchFutures) {
            jokes.addAll(future.get());
        }

        return jokes;
    }

    private CompletableFuture<List<Joke>> fetchJokesAsync(int count) {
        return CompletableFuture.supplyAsync(() -> IntStream.range(0, count)
                .mapToObj(j -> restTemplate.getForObject(url, Joke.class))
                .collect(Collectors.toList()));
    }
}
