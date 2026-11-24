
package com.artfolio;

import com.artfolio.resources.JakartaEE11Resource;
import com.artfolio.resources.ObrasResource;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import java.util.HashSet;
import java.util.Set;

@ApplicationPath("resources")
public class JakartaRestConfiguration extends Application {

    @Override
    public Set<Class<?>> getClasses() {

        Set<Class<?>> recursos = new HashSet<>();

        recursos.add(JakartaEE11Resource.class);
        recursos.add(ObrasResource.class);

        return recursos;
    }
}
