package com.mycompany.services;


import com.mycompany.model.Beer;
import fr.ybonnel.simpleweb4j.exception.HttpErrorException;
import fr.ybonnel.simpleweb4j.handlers.resource.RestResource;

import java.util.Collection;

public class BeerRessource extends RestResource<Beer> {

    public BeerRessource(String route) {
        super(route, Beer.class);
    }

    @Override
    public Beer getById(String id) throws HttpErrorException {
        return Beer.simpleEntityManager.getById(Long.parseLong(id));
    }

    @Override
    public Collection<Beer> getAll() throws HttpErrorException {
        return Beer.simpleEntityManager.getAll();
    }

    @Override
    public void update(String id, Beer resource) throws HttpErrorException {
        resource.setId(Long.parseLong(id));
        Beer.simpleEntityManager.update(resource);
    }

    @Override
    public Beer create(Beer resource) throws HttpErrorException {
        Beer.simpleEntityManager.save(resource);
        return resource;
    }

    @Override
    public void delete(String id) throws HttpErrorException {
        Beer.simpleEntityManager.delete(Long.parseLong(id));
    }
}
