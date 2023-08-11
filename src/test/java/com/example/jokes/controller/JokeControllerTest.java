package com.example.jokes.controller;

import com.example.jokes.model.Joke;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(JokeController.class)
@AutoConfigureMockMvc
public class JokeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JokeController jokeController;

    @Test
    public void testGetJokes() throws Exception {
        List<Joke> jokes = getJokesArray(5);
        when(jokeController.getJokes(anyInt())).thenReturn(jokes);

        mockMvc.perform(get("/jokes").param("count", "5"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetJokesCountTooLarge() throws Exception {
        mockMvc.perform(get("/jokes").param("count", "101"))
                .andExpect(status().isBadRequest());
    }

    private List<Joke> getJokesArray(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> new Joke("Joke " + i, "Text " + i))
                .collect(Collectors.toList());
    }


}