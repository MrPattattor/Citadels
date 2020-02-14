package com.montaury.citadels;

import com.montaury.citadels.district.Card;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

public class CityTest {
    City city;

    @Before
    public void setUp() {
        Board board = new Board();
        city = new City(board);
    }

    @Test
    public void construction_made() {
        Possession possession = new Possession(0, null); // 0 ou donc 0 score
        city.buildDistrict(Card.MANOR_5); // score +3
        city.buildDistrict(Card.WATCHTOWER_2); // +1 score
        city.buildDistrict(Card.TAVERN_5); // +1 score
        int score = city.score(possession);
        Assertions.assertThat(score).isEqualTo(5);
    }

    @Test
    public void first_player_with_completed_city() {
        Possession possession = new Possession(0, null); // 0 or donc 0 score
        city.buildDistrict(Card.WATCHTOWER_2); // +1 score
        city.buildDistrict(Card.MANOR_5); // +3 score
        city.buildDistrict(Card.TRADING_POST_1); // +2 score
        city.buildDistrict(Card.TAVERN_5); // +1 score
        city.buildDistrict(Card.TOWN_HALL_1); // +5 score
        city.buildDistrict(Card.TOWN_HALL_2); // +5 score
        city.buildDistrict(Card.CHURCH_3); // +2 score
        int score = city.score(possession);
        Assertions.assertThat(score).isEqualTo(23); // 19 points + 4 points car c'est le premier joueur à avoir la cité est complétée -> 23 au total

    }

    @Test
    public void give_bonus_points_for_all_types_districts() {
        Possession possession = new Possession(0, null);
        city.buildDistrict(Card.PALACE_3); // +5,NOBLE
        city.buildDistrict(Card.BATTLEFIELD_2); // +3,MILITARY
        city.buildDistrict(Card.DOCKS_3); // +3,TRADE
        city.buildDistrict(Card.CHURCH_3); // +2,RELIGIOUS
        city.buildDistrict(Card.KEEP_1); // +3,SPECIAL
        int score = city.score(possession);
        Assertions.assertThat(score).isEqualTo(19); // Normalement : 16 + Bonus 5 types : 3 points -> 19 au total
    }
}



