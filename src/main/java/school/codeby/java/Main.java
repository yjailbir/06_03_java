package school.codeby.java;

import org.apache.catalina.LifecycleException;
import school.codeby.java.server.Server;

import java.io.IOException;

public class Main {
  public static void main(String[] args) throws LifecycleException, IOException {
    final var server = new Server(8888);
    server.addHandler("/", (req, resp) -> resp.getWriter().write("It works!"));

    server.start();
    server.await();
  }
}
