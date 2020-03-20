package com.montaury.citadels;

import com.montaury.citadels.district.Card;
import com.montaury.citadels.district.District;
import com.montaury.citadels.player.Player;
import com.montaury.citadels.round.action.DestroyDistrictAction;
import io.vavr.collection.HashSet;
import org.junit.Before;
import org.junit.Test;
import org.assertj.core.api.Assertions;

public class DestroyableDistrictTest {

    private Board board;
    private City city;

    @Before
    public void set_up() {
        board = new Board();
        city = new City(board);
    }

    @Test
    public void destroyable_district_is_destroyed() {
        city.buildDistrict(Card.KEEP_1);
        city.buildDistrict(Card.MARKET_3);
        city.destroyDistrict(Card.MARKET_3);
        Assertions.assertThat(city.districts().size()).isEqualTo(1);
    }

}
