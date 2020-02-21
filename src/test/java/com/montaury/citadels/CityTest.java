package com.montaury.citadels;

import com.montaury.citadels.district.Card;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

public class CityTest {
    City city;
    Board board;
    Possession possession;

    @Before
    public void setUp() {
        board = new Board();
        city = new City(board);
        possession = new Possession(0, null);
    }

    @Test
    public void construction_made() {
        city.buildDistrict(Card.MANOR_5); // score +3
        city.buildDistrict(Card.WATCHTOWER_2); // +1 score
        city.buildDistrict(Card.TAVERN_5); // +1 score
        int score = city.score(possession);
        Assertions.assertThat(score).isEqualTo(5);
    }

    @Test
    public void first_player_with_completed_city() {
        city.buildDistrict(Card.WATCHTOWER_2); // +1 score
        city.buildDistrict(Card.MANOR_5); // +3 score
        city.buildDistrict(Card.TRADING_POST_1); // +2 score
        city.buildDistrict(Card.TAVERN_5); // +1 score
        city.buildDistrict(Card.TOWN_HALL_1); // +5 score
        city.buildDistrict(Card.TOWN_HALL_2); // +5 score
        city.buildDistrict(Card.CHURCH_3); // +2 score
        int score = city.score(possession);
        Assertions.assertThat(score).isEqualTo(23); // 19 points + 4 points car c'est le premier joueur à avoir la cité complétée -> 23 au total
    }

    @Test
    public void give_bonus_points_for_dragon_gate() {
        city.buildDistrict(Card.DRAGON_GATE); // + 8 score
        city.buildDistrict(Card.HARBOR_2); // + 4 score
        int score = city.score(possession);
        Assertions.assertThat(score).isEqualTo(12); // 4 points pour harbor mais aussi les 8 points de la Merveille
    }

    @Test
    public void give_bonus_points_for_all_types_districts() {
        city.buildDistrict(Card.PALACE_3); // +5,NOBLE
        city.buildDistrict(Card.BATTLEFIELD_2); // +3,MILITARY
        city.buildDistrict(Card.DOCKS_3); // +3,TRADE
        city.buildDistrict(Card.CHURCH_3); // +2,RELIGIOUS
        city.buildDistrict(Card.KEEP_1); // +3,SPECIAL
        int score = city.score(possession);
        Assertions.assertThat(score).isEqualTo(19); // Normalement : 16 + Bonus 5 types : 3 points -> 19 au total
    }

    @Test
    public void second_player_with_completed_city() {
        City city2 = new City(board);
        city.buildDistrict(Card.WATCHTOWER_2); // +1 score
        city.buildDistrict(Card.MANOR_5); // +3 score
        city.buildDistrict(Card.TRADING_POST_1); // +2 score
        city.buildDistrict(Card.TAVERN_5); // +1 score
        city.buildDistrict(Card.TOWN_HALL_1); // +5 score
        city.buildDistrict(Card.TOWN_HALL_2); // +5 score
        city.buildDistrict(Card.CHURCH_3); // +2 score
        city2.buildDistrict(Card.WATCHTOWER_2); // +1 score
        city2.buildDistrict(Card.MANOR_5); // +3 score
        city2.buildDistrict(Card.TRADING_POST_1); // +2 score
        city2.buildDistrict(Card.TAVERN_5); // +1 score
        city2.buildDistrict(Card.TOWN_HALL_1); // +5 score
        city2.buildDistrict(Card.TOWN_HALL_2); // +5 score
        city2.buildDistrict(Card.CHURCH_3); // +2 score
        int score = city2.score(possession);
        Assertions.assertThat(score).isEqualTo(21); // 19 points + 2 points car c'est le second joueur à avoir la cité complétée -> 21 au total
    }
}



