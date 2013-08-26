package com.mycompany;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mycompany.model.Beer;
import fr.ybonnel.simpleweb4j.model.SimpleEntityManager;
import fr.ybonnel.simpleweb4j.test.SimpleWeb4jTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static fr.ybonnel.simpleweb4j.SimpleWeb4j.stop;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class BeerTest extends SimpleWeb4jTest {

    @Before
    public void setup() {

        Main.startServer(getPort(), false);

        SimpleEntityManager.openSession().beginTransaction();

        for (Beer beerToDelete : Beer.simpleEntityManager.getAll()) {
            Beer.simpleEntityManager.delete(beerToDelete.getId());
        }

        SimpleEntityManager.getCurrentSession().getTransaction().commit();
        SimpleEntityManager.closeSession();
    }

    @After
    public void tearDown() {
        stop();
    }



    @Test
    public void should_return_no_member() {
        List<Beer> beers = new Gson().fromJson(HttpRequest.get(defaultUrl() + "/beer").body(), new TypeToken<List<Class>>(){}.getType());
        assertTrue(beers.isEmpty());
    }

    public Long insertMember() {
        Beer beer = new Beer();
        beer.setName("name");
        Gson gson = new Gson();
        assertEquals(201, HttpRequest.post(defaultUrl() + "/beer").send(gson.toJson(beer)).code());

        List<Beer> beers = gson.fromJson(HttpRequest.get(defaultUrl() + "/beer").body(), new TypeToken<List<Beer>>(){}.getType());
        assertEquals(1, beers.size());
        assertEquals("name", beers.get(0).getName());
        return beers.get(0).getId();
    }

    @Test
    public void should_create_and_get_by_id() {
        long id = insertMember();

        Beer newBeer = new Gson().fromJson(HttpRequest.get(defaultUrl() + "/beer/" + id).body(), Beer.class);
        assertNotNull(newBeer);
        assertEquals(id, newBeer.getId().longValue());
        assertEquals("name", newBeer.getName());
    }

    @Test
    public void should_update() {
        long id = insertMember();
        Gson gson = new Gson();

        Beer newBeer = new Beer();
        newBeer.setName("newName");

        assertEquals(204, HttpRequest.put(defaultUrl() + "/beer/" + id).send(gson.toJson(newBeer)).code());

        List<Beer> beers = gson.fromJson(HttpRequest.get(defaultUrl() + "/beer").body(), new TypeToken<List<Beer>>(){}.getType());
        assertEquals(1, beers.size());
        assertEquals("newName", beers.get(0).getName());
    }

    @Test
    public void should_delete() {
        long id = insertMember();
        Gson gson = new Gson();

        assertEquals(204, HttpRequest.delete(defaultUrl() + "/beer/" + id).code());
        List<Beer> beers = gson.fromJson(HttpRequest.get(defaultUrl() + "/beer").body(), new TypeToken<List<Beer>>(){}.getType());
        assertTrue(beers.isEmpty());
    }

}
