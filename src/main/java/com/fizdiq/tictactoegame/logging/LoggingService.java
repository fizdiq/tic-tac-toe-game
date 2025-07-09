package com.fizdiq.tictactoegame.logging;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface LoggingService {

    void logRequest(HttpServletRequest httpServletRequest, Object body) throws IOException;

    void logResponse(HttpServletRequest httpServletRequest,
                     HttpServletResponse httpServletResponse,
                     Object body);
}
