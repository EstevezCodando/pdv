package com.pdv.pontovenda.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Handler global de exceções — implementa o padrão fail gracefully.
 * Intercepta exceções não tratadas pelos controllers e retorna:
 *   - Para requisições web (MVC): pagina de erro segura, sem stack trace.
 *   - Para requisições API (REST/JSON): JSON com mensagem segura e codigo HTTP.
 * Nenhuma informação técnica é exposta ao usuário final (segurança).
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String MENSAGEM_ERRO_GENERICO = "Ocorreu um erro interno. Tente novamente mais tarde.";

    /** Recurso não encontrado (404) — entidade de negocio. */
    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public Object handleRecursoNaoEncontrado(RecursoNaoEncontradoException ex, HttpServletRequest request) {
        logger.warn("Recurso nao encontrado: {}", ex.getMessage());

        if (isApiRequest(request)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(criarErroApi(HttpStatus.NOT_FOUND.value(), ex.getMessage()));
        }

        ModelAndView mv = new ModelAndView("error");
        mv.addObject("status", 404);
        mv.addObject("mensagem", ex.getMessage());
        mv.setStatus(HttpStatus.NOT_FOUND);
        return mv;
    }

    /** Violação de regra de negocio (422). */
    @ExceptionHandler(RegraDeNegocioException.class)
    public Object handleRegraDeNegocio(RegraDeNegocioException ex, HttpServletRequest request) {
        logger.warn("Regra de negocio violada: {}", ex.getMessage());

        if (isApiRequest(request)) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(criarErroApi(HttpStatus.UNPROCESSABLE_ENTITY.value(), ex.getMessage()));
        }

        ModelAndView mv = new ModelAndView("error");
        mv.addObject("status", 422);
        mv.addObject("mensagem", ex.getMessage());
        mv.setStatus(HttpStatus.UNPROCESSABLE_ENTITY);
        return mv;
    }

    /** Erro de validação de campos — @Valid (400). */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidacao(MethodArgumentNotValidException ex) {
        logger.warn("Erro de validacao: {}", ex.getMessage());

        Map<String, String> errosCampos = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(erro ->
                errosCampos.put(erro.getField(), erro.getDefaultMessage()));

        Map<String, Object> resposta = new HashMap<>();
        resposta.put("status", HttpStatus.BAD_REQUEST.value());
        resposta.put("erro", "Dados invalidos");
        resposta.put("campos", errosCampos);

        return ResponseEntity.badRequest().body(resposta);
    }

    /** Recurso HTTP não encontrado (404) — rota inexistente. */
    @ExceptionHandler(NoResourceFoundException.class)
    public Object handleNoResourceFound(NoResourceFoundException ex, HttpServletRequest request) {
        logger.warn("Rota nao encontrada: {}", request.getRequestURI());

        if (isApiRequest(request)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(criarErroApi(HttpStatus.NOT_FOUND.value(), "Recurso nao encontrado"));
        }

        ModelAndView mv = new ModelAndView("error");
        mv.addObject("status", 404);
        mv.addObject("mensagem", "Pagina nao encontrada");
        mv.setStatus(HttpStatus.NOT_FOUND);
        return mv;
    }

    /** Argumento ilegal (400) — fail early nos services. */
    @ExceptionHandler(IllegalArgumentException.class)
    public Object handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        logger.warn("Argumento invalido: {}", ex.getMessage());

        if (isApiRequest(request)) {
            return ResponseEntity.badRequest()
                    .body(criarErroApi(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
        }

        ModelAndView mv = new ModelAndView("error");
        mv.addObject("status", 400);
        mv.addObject("mensagem", "Requisicao invalida: " + ex.getMessage());
        mv.setStatus(HttpStatus.BAD_REQUEST);
        return mv;
    }

    /**
     * Captura QUALQUER exceção não prevista — fail gracefully.
     * Registra o erro internamente (log) mas retorna mensagem genérica ao usuário,
     * sem expor stack traces, nomes de classes ou informações do servidor.
     */
    @ExceptionHandler(Exception.class)
    public Object handleErroGenerico(Exception ex, HttpServletRequest request) {
        logger.error("Erro inesperado em {}: {}", request.getRequestURI(), ex.getMessage(), ex);

        if (isApiRequest(request)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(criarErroApi(HttpStatus.INTERNAL_SERVER_ERROR.value(), MENSAGEM_ERRO_GENERICO));
        }

        ModelAndView mv = new ModelAndView("error");
        mv.addObject("status", 500);
        mv.addObject("mensagem", MENSAGEM_ERRO_GENERICO);
        mv.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        return mv;
    }

    /** Verifica se a requisição espera JSON (API REST) ou HTML (MVC). */
    private boolean isApiRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String accept = request.getHeader("Accept");
        return uri.startsWith("/api/")
                || (accept != null && accept.contains("application/json"));
    }

    /** Monta corpo padrao de erro para respostas API. */
    private Map<String, Object> criarErroApi(int status, String mensagem) {
        Map<String, Object> erro = new HashMap<>();
        erro.put("status", status);
        erro.put("erro", mensagem);
        return erro;
    }
}
