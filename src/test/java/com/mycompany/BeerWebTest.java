package com.mycompany;


import com.mycompany.model.Beer;
import fr.ybonnel.simpleweb4j.model.SimpleEntityManager;
import fr.ybonnel.simpleweb4j.test.SimpleWeb4jTest;
import org.fluentlenium.core.domain.FluentList;
import org.fluentlenium.core.domain.FluentWebElement;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static fr.ybonnel.simpleweb4j.SimpleWeb4j.stop;
import static org.fest.assertions.Assertions.assertThat;

public class BeerWebTest extends SimpleWeb4jTest {

    @Before
    public void setup() {
        Main.startServer(getPort(), false);
        SimpleEntityManager.openSession().beginTransaction();

        for (Beer beerToDelete : Beer.simpleEntityManager.getAll()) {
            Beer.simpleEntityManager.delete(beerToDelete.getId());
        }
        SimpleEntityManager.getCurrentSession().getTransaction().commit();
        SimpleEntityManager.closeSession();
        goTo("/");
    }

    @After
    public void tearDown() {
        stop();
    }



    @Test
    public void should_not_have_beer() {
        assertThat(find("#beers")).isNotEmpty();
        assertThat(find("tbody tr")).isEmpty();
    }

    private void insertBeer(String nameOfBeer) {
        click("#addBeer");
        await().atMost(3, TimeUnit.SECONDS).until("#beers").isNotPresent();
        fill("#name").with(nameOfBeer);
        click("#submit");
    }

    @Test
    public void can_insert_beer() {
        insertBeer("name");
        FluentList<FluentWebElement> trInTbody = find("tbody tr");
        assertThat(trInTbody).hasSize(1);
        FluentWebElement oneBeer = trInTbody.get(0);
        assertThat(oneBeer.findFirst("td").getText()).isEqualTo("name");
    }

    @Test
    public void can_update_beer() {
        insertBeer("name");
        click("a.icon-edit");
        await().atMost(3, TimeUnit.SECONDS).until("#beers").isNotPresent();
        clear("#name");
        fill("#name").with("newName");
        click("#submit");
        FluentList<FluentWebElement> trInTbody = find("tbody tr");
        assertThat(trInTbody).hasSize(1);
        FluentWebElement oneBeer = trInTbody.get(0);
        assertThat(oneBeer.findFirst("td").getText()).isEqualTo("newName");
    }

    @Test
    public void can_delete_beer() {
        insertBeer("name");
        click("a.icon-remove");
        click("#remove");
        assertThat(find("tbody tr")).isEmpty();
    }


}
