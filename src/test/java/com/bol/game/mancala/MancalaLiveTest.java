package com.bol.game.mancala;

import com.bol.game.mancala.entities.Enums.BolcomEnums;
import com.bol.game.mancala.entities.Pit;
import com.bol.game.mancala.entities.Request;
import com.bol.game.mancala.entities.Status;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MancalaApplication.class)
public class MancalaLiveTest {
	public static final String API_ROOT = "http://localhost:8080/api/game";

	@Autowired
	TestHelper testHelper;

	@Test
	public void whenStartNewGame_thenAccepted() {
		Request request= testHelper.createRandomNewGame();
		final Response response = RestAssured.given().contentType(MediaType.APPLICATION_JSON_VALUE).body(request)
				.post(API_ROOT );
		Assert.assertEquals(HttpStatus.ACCEPTED.value(), response.getStatusCode());
	}

	@Test
	public void whenMove_thenAccepted(){
		Request request = testHelper.createMoveRequest();
		final Response response = RestAssured.given().contentType(MediaType.APPLICATION_JSON_VALUE).body(request)
				.post(API_ROOT );
		Assert.assertEquals(HttpStatus.ACCEPTED.value(), response.getStatusCode());
	}

	@Test
	public void whenMove_thenStatusIsCorrect(){
		Request request = testHelper.createMoveRequest();
		int pitNo= Integer.parseInt(request.getPitNumber().replaceAll("pit",""));
		final Status response = RestAssured.given().contentType(MediaType.APPLICATION_JSON_VALUE).body(request)
				.post(API_ROOT ).then().statusCode(HttpStatus.ACCEPTED.value()).extract().as(Status.class);
		Assert.assertEquals(1, response.getStates().get(response.getStates().size()-1).getPlayer1().getLargePit());
		Pit pit=response.getStates().get(response.getStates().size()-1).getPlayer1().getPit();
		Assert.assertTrue(pit.getPit1()==0 || pit.getPit2()==0|| pit.getPit3()==0|| pit.getPit4()==0|| pit.getPit5()==0|| pit.getPit6()==0);
		if(pitNo!=1){
			Assert.assertEquals(BolcomEnums.Player.player2.getName(), response.getTurn());
		}else {
			Assert.assertEquals(BolcomEnums.Player.player1.getName(), response.getTurn());
		}
		Assert.assertEquals(BolcomEnums.GameStatus.CONTINUE.getValue(), response.getStatus());
		for (int i=1; i<pitNo; i++){
			String s= String.format("pit%d", i);
			Assert.assertEquals((Integer) 6, (Integer) testHelper.invokeGetter(pit, testHelper.getMethod(s,pit.getClass(),"getter")));
		}
		int cnt=0;
		for(int i=pitNo+1; i<7;i++){
			cnt++;
			String s= String.format("pit%d", i);
			Assert.assertEquals((Integer) 7, (Integer) testHelper.invokeGetter(pit, testHelper.getMethod(s,pit.getClass(),"getter")));

		}
		Pit pit2=response.getStates().get(response.getStates().size()-1).getPlayer2().getPit();
		cnt=5-cnt;
		for(int i=1; i<=cnt; i++){
			String s= String.format("pit%d", i);
			Assert.assertEquals((Integer) 7, (Integer) testHelper.invokeGetter(pit2, testHelper.getMethod(s,pit2.getClass(),"getter")));
		}
	}

	@Test
	public void whenIncorrectMove_thenBadRequest(){
		Request request = testHelper.createMoveRequest();
		request.setPitNumber(String.format("pit%d", 7));
		final Response response = RestAssured.given().contentType(MediaType.APPLICATION_JSON_VALUE).body(request)
				.post(API_ROOT );
		Assert.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
	}

	@Test
	public void whenBonusMove_thenStatusIsCorrect(){
		Request request= testHelper.createRandomNewGame();
		Response response = RestAssured.given().contentType(MediaType.APPLICATION_JSON_VALUE).body(request)
				.post(API_ROOT );
		Assert.assertEquals(HttpStatus.ACCEPTED.value(), response.getStatusCode());
		testHelper.playMove(request.getId(), BolcomEnums.Player.player1.getName(), 2);
		testHelper.playMove(request.getId(), BolcomEnums.Player.player2.getName(), 1);
		testHelper.playMove(request.getId(), BolcomEnums.Player.player1.getName(), 1);
		testHelper.playMove(request.getId(), BolcomEnums.Player.player2.getName(), 2);
		testHelper.playMove(request.getId(), BolcomEnums.Player.player1.getName(), 1);
		request = new Request(request.getId(), BolcomEnums.Player.player2.getName(), "pit1");
		final Status response2 = RestAssured.given().contentType(MediaType.APPLICATION_JSON_VALUE).body(request)
				.post(API_ROOT ).then().statusCode(HttpStatus.ACCEPTED.value()).extract().as(Status.class);
		Pit pit=response2.getStates().get(response2.getStates().size()-1).getPlayer1().getPit();
		Pit pit2=response2.getStates().get(response2.getStates().size()-1).getPlayer2().getPit();
		Assert.assertEquals(0, pit.getPit5());
		Assert.assertEquals(0, pit2.getPit2());
		Assert.assertEquals(11, response2.getStates().get(response2.getStates().size()-1).getPlayer2().getLargePit());
		Assert.assertEquals(BolcomEnums.Player.player2.getName(), response2.getTurn());
		Assert.assertEquals(BolcomEnums.GameStatus.CONTINUE.getValue(), response2.getStatus());
	}
}
