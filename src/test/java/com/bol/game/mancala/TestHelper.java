package com.bol.game.mancala;

import com.bol.game.mancala.Domain.Game;
import com.bol.game.mancala.entities.Enums.BolcomEnums;
import com.bol.game.mancala.entities.Request;
import com.bol.game.mancala.service.MancalaService;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;
@Service
public class TestHelper {
	private final static Logger logger = Logger.getLogger(MancalaLiveTest.class);
	@Autowired
	MancalaService mancalaService;
	public Request createRandomNewGame(){
		Random r = new Random();
		return new Request(r.nextInt(), BolcomEnums.Player.player1.getName(), null);
	}

	public Request createMoveRequest(){
		Random r = new Random();
		Game game= new Game();
		int gameId=r.nextInt();
		MancalaService.games.put(gameId, game);
		return new Request(gameId, BolcomEnums.Player.player1.getName(), "pit"+(r.nextInt(6)+1));
	}

	public Method getMethod(String variableName,Class aClass,String getterOrSetter)
	{

		Method[] declaredMethods = aClass.getDeclaredMethods();
		for (Method method:declaredMethods) {
			if(getterOrSetter.equalsIgnoreCase("getter"))
			{
				if(isGetter(method) && method.getName().toUpperCase().contains(variableName.toUpperCase()))
				{
					return method;
				}
			}
			if(getterOrSetter.equalsIgnoreCase("setter"))
			{
				if(isSetter(method) && method.getName().toUpperCase().contains(variableName.toUpperCase()))
				{
					return method;
				}
			}
		}
		return null;
	}
	private static boolean isGetter(Method method){
		// check for getter methods
		if((method.getName().startsWith("get") || method.getName().startsWith("is"))
				&& method.getParameterCount() == 0 && !method.getReturnType().equals(void.class)){
			return true;
		}
		return false;
	}

	private static boolean isSetter(Method method){
		// check for setter methods
		if(method.getName().startsWith("set") && method.getParameterCount() == 1
				&& method.getReturnType().equals(void.class)){
			return true;
		}
		return false;
	}

	public void invokeSetter(Object obj,Object variableValue,Method setter)
	{
		try {
			setter.invoke(obj,variableValue);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}

	}

	public Object invokeGetter(Object obj,Method getter)
	{
		try {
			Object f = getter.invoke(obj);
			return f;
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			logger.info(e.getMessage());
		}
		return null;
	}

	public void playMove(int requestId, String player, int pitNo){
		Request request= new Request(requestId, player, String.format("pit%d", pitNo));
		RestAssured.given().contentType(MediaType.APPLICATION_JSON_VALUE).body(request)
					.post(MancalaLiveTest.API_ROOT );
	}
}
