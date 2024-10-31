package school.codeby.java.server;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Server {
  private final Tomcat tomcat;
  private final Context ctx;

  public Server(final int port) throws IOException {
    tomcat = new Tomcat();
    tomcat.setPort(port);
    final Connector connector = new Connector();
    connector.setPort(port);
    tomcat.setConnector(connector);

    final File docBase = Files.createTempDirectory("tomcat").toFile();
    docBase.deleteOnExit();

    ctx = tomcat.addContext("", docBase.getAbsolutePath());
  }

  public void start() throws LifecycleException {
    tomcat.start();
  }

  public void await() {
    tomcat.getServer().await();
  }

  public void stop() throws LifecycleException {
    tomcat.stop();
  }

  public void addHandler(final String path, final HttpHandler handler) {
    final String servletName = handler.getClass().getName();
    final Wrapper servlet = Tomcat.addServlet(ctx, servletName, new HttpServlet() {
      @Override
      protected void service(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
        handler.service(req, resp);
      }
    });
    servlet.setLoadOnStartup(1);
    ctx.addServletMappingDecoded(path, servletName);
  }

  public interface HttpHandler {
    void service(HttpServletRequest req, HttpServletResponse resp) throws IOException;
  }
}
