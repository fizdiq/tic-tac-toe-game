package com.fizdiq.tictactoegame;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class TicTacToeGameApplication {

    public static void main(String[] args) {
        SpringApplication.run(TicTacToeGameApplication.class, args);
        log.info("=========== App has Started ===========");
    }

}
