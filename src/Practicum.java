import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Practicum {
    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        HttpServer httpServer = HttpServer.create();

        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/hello", new HelloHandler());
        httpServer.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
        //httpServer.stop(1);
    }

    static class HelloHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String response;

            // извлеките метод из запроса
            String method = httpExchange.getRequestMethod();
            // System.out.println("method = " + method); // test

            switch(method) {
                // сформируйте ответ в случае, если был вызван POST-метод
                case "POST":
                    // извлеките тело запроса
                    InputStream inputStream = httpExchange.getRequestBody();
                    String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                    System.out.println("body = " + body); // test

                    // извлеките path из запроса
                    String path = httpExchange.getRequestURI().getPath();
                    // а из path — профессию и имя
                    String profession = path.split("/")[2];
                    String name = path.split("/")[3];
                    // System.out.println("path = " + path); // test
                    // System.out.println("profession = " + profession); // test
                    // System.out.println("name = " + name); // test

                    // извлеките заголовок
                    Headers requestHeaders = httpExchange.getRequestHeaders();
                    List<String> wishGoodDay = requestHeaders.get("X-Wish-Good-Day");
                    // System.out.println("wishGoodDay = " + wishGoodDay); // test
                    // System.out.println("wishGoodDay.contains(true) = " + wishGoodDay.contains("true")); // test
                    // System.out.println("if condition = " + ((wishGoodDay != null) && (wishGoodDay.contains("true")))); // test

                    // соберите ответ
                    if ((wishGoodDay != null) && (wishGoodDay.contains("true"))) {
                        response = body + ", " + profession + " " + name + "! " + "Хорошего дня!";
                    } else {
                        response = body + ", " + profession + " " + name + "! ";
                    }
                    break;
                case "GET":
                    response = "Здравствуйте!";
                    break;
                default:
                    // не забудьте про ответ для остальных методов
                    response = "Некорректный метод!";
            }

            httpExchange.sendResponseHeaders(200, 0);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }
}