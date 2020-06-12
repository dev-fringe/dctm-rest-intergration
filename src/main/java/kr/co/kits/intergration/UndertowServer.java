package kr.co.kits.intergration;

import static io.undertow.servlet.Servlets.servlet;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;

import org.h2.server.web.WebServlet;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.resource.FileResourceManager;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.ListenerInfo;
import io.undertow.servlet.util.ImmediateInstanceFactory;

public class UndertowServer {
    
    @Bean(name = "undertow")
    public Undertow undertow() throws ServletException, IOException {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.setConfigLocation(Application.class.getPackage().getName()+".config");
        DeploymentInfo servletBuilder = Servlets.deployment().setClassLoader(this.getClass().getClassLoader())
                .setContextPath("/").setDeploymentName(Application.class.getName() + ".war")
                .addServlet(Servlets.servlet("dctm-rest-integration-DispatcherServlet", DispatcherServlet.class, new ImmediateInstanceFactory<>(new DispatcherServlet(context)))
                .addMapping("/").setLoadOnStartup(1))
				.setResourceManager(new FileResourceManager(new File("src/main/webapp"), 1024))
                .addListener(new ListenerInfo(ContextLoaderListener.class, new ImmediateInstanceFactory<>(new ContextLoaderListener(context))));
        servletBuilder.addServlet(servlet("h2-console-web", WebServlet.class).addMapping("/h2/*"));
        DeploymentManager manager = Servlets.defaultContainer().addDeployment(servletBuilder);
        manager.deploy();
        PathHandler path = Handlers.path(Handlers.redirect("/")).addPrefixPath("/", manager.start());
        return Undertow.builder().addHttpListener(80, "0.0.0.0").setHandler(path).build();
    }
}