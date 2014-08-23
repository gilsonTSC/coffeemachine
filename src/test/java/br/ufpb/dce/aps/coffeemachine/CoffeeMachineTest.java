package br.ufpb.dce.aps.coffeemachine;

import static org.mockito.Matchers.anyDouble;
import static org.mockito.Mockito.*;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

public abstract class CoffeeMachineTest {

	protected ComponentsFactory factory;
	protected CoffeeMachine facade;

	protected Display display;
	protected CashBox cashBox;
	protected Dispenser coffeePowderDispenser;
	protected Dispenser waterDispenser;
	protected Dispenser cupDispenser;
	protected DrinkDispenser drinkDispenser;
	protected Dispenser sugarDispenser;
	protected Dispenser creamerDispenser;
	protected Dispenser bouillonDispenser;

	protected CoffeeMachine createFacade(ComponentsFactory factory) {
		CoffeeMachine coffeeMachine = null;

		try {
			@SuppressWarnings("unchecked")
			Class<CoffeeMachine> clazz = (Class<CoffeeMachine>) Class
					.forName("br.ufpb.dce.aps.coffeemachine.impl.MyCoffeeMachine");

			coffeeMachine = clazz.newInstance();
			coffeeMachine.setFactory(factory);

		} catch (ClassNotFoundException e) {
			Assert.fail();
		} catch (InstantiationException e) {
			Assert.fail();
		} catch (IllegalAccessException e) {
			Assert.fail();
		}

		return coffeeMachine;
	}

	@Before
	public void init() {
		factory = new MockComponentsFactory();
		display = factory.getDisplay();
		cashBox = factory.getCashBox();
		coffeePowderDispenser = factory.getCoffeePowderDispenser();
		waterDispenser = factory.getWaterDispenser();
		cupDispenser = factory.getCupDispenser();
		drinkDispenser = factory.getDrinkDispenser();
		sugarDispenser = factory.getSugarDispenser();
		creamerDispenser = factory.getCreamerDispenser();
		bouillonDispenser = factory.getBouillonDispenser();
	}

	@After
	public void genericVerifications() {
		verifyNoMoreInteractions(mocks());
	}

	@Test
	public void createFacade() {
		// Operation under test
		facade = createFacade(factory);

		// Verification
		verifyNewSession(null);
	}

	protected void verifyNewSession(InOrder inOrder) {
		if (inOrder == null) {
			verify(display).info(Messages.INSERT_COINS);
		} else {
			inOrder.verify(display).info(Messages.INSERT_COINS);
		}
	}

	@Test
	public void insertCoin() {
		// Preparing scenario
		facade = createFacade(factory);
		resetMocks();

		// Operation under test
		facade.insertCoin(Coin.dime);

		// Verification
		verifySessionMoney("0.10");
	}

	protected void verifySessionMoney(String value) {
		verify(display).info("Total: US$ " + value);
	}

	@Test
	public void insertCoins() {
		// Preparing scenario
		facade = createFacade(factory);
		facade.insertCoin(Coin.halfDollar);
		resetMocks();

		// Operation under test
		facade.insertCoin(Coin.nickel);

		// Verification
		verifySessionMoney("0.55");
	}

	@Test(expected = CoffeeMachineException.class)
	public void nullCoin() {
		// Preparing scenario
		facade = createFacade(factory);
		resetMocks();

		// Operation under test
		facade.insertCoin(null);
	}

	@Test(expected = CoffeeMachineException.class)
	public void cancelWithoutCoins() {
		// Preparing scenario
		facade = createFacade(factory);
		resetMocks();

		// Operation under test
		facade.cancel();
	}

	@Test
	public void cancelWithOneCoin() {
		InOrder inOrder = prepareScenarioWithCoins(Coin.halfDollar);

		// Operation under test
		facade.cancel();

		// Verification
		verifyCancel(inOrder, Coin.halfDollar);
	}

	protected InOrder prepareScenarioWithCoins(Coin... coins) {
		facade = createFacade(factory);
		insertCoins(coins);
		return resetMocks();
	}

	@Test
	public void cancelWithTwoCoins() {
		InOrder inOrder = prepareScenarioWithCoins(Coin.penny, Coin.nickel);

		// Operation under test
		facade.cancel();

		// Verification
		verifyCancel(inOrder, Coin.nickel, Coin.penny);
	}

	@Test
	public void cancelWithPossibleDifferentChange() {
		InOrder inOrder = prepareScenarioWithCoins(Coin.quarter, Coin.quarter);

		// Operation under test
		facade.cancel();

		// Verification
		verifyCancelMessage(inOrder);
		verifyReleaseCoins(inOrder, Coin.quarter, 2);
		verifyNewSession(inOrder);
	}

	@Test
	public void selectBlackWithoutChange() {
		InOrder inOrder = prepareScenarioWithCoins(Coin.quarter, Coin.dime);

		// Simulating returns
		doContainBlackIngredients();

		// Operation under test
		facade.select(Drink.BLACK);

		// Verification
		verifyBlackPlan(inOrder);
		verifyBlackMix(inOrder);
		verifyDrinkRelease(inOrder);
		verifyNewSession(inOrder);
	}

	protected void doContainBlackIngredients() {
		doContain(coffeePowderDispenser, anyDouble());
		doContain(waterDispenser, anyDouble());
		doContain(cupDispenser, 1);
	}

	protected void verifyBlackPlan(InOrder inOrder) {
		inOrder.verify(cupDispenser).contains(1);
		inOrder.verify(waterDispenser).contains(100);
		inOrder.verify(coffeePowderDispenser).contains(15);
	}

	protected void verifyBlackMix(InOrder inOrder) {
		inOrder.verify(display).info(Messages.MIXING);
		inOrder.verify(coffeePowderDispenser).release(15);
		inOrder.verify(waterDispenser).release(100);
	}

	@Test
	public void twoDrinks() {
		// Preparing scenario: first drink
		validSession(Drink.BLACK, Coin.dime, Coin.quarter);

		// Preparing scenario: second drink
		insertCoins(Coin.dime, Coin.quarter);
		InOrder inOrder = resetMocks();

		// Simulating returns
		doContainBlackSugarIngredients();

		// Operation under test
		facade.select(Drink.BLACK_SUGAR);

		// Verification
		verifyBlackSugarPlan(inOrder);
		verifyBlackSugarMix(inOrder);
		verifyDrinkRelease(inOrder);
		verifyNewSession(inOrder);
	}

	@SuppressWarnings("incomplete-switch")
	protected void validSession(Drink drink, Coin... coins) {
		facade = createFacade(factory);
		insertCoins(coins);

		switch (drink) {
		case BLACK:
			doContainBlackIngredients();
			break;
		case BLACK_SUGAR:
			doContainBlackSugarIngredients();
			break;
		}

		facade.select(drink);
	}

	protected void doContainBlackSugarIngredients() {
		doContainBlackIngredients();
		doContain(sugarDispenser, anyDouble());
	}

	protected void verifyBlackSugarPlan(InOrder inOrder) {
		verifyBlackPlan(inOrder);
		inOrder.verify(sugarDispenser).contains(5);
	}

	protected void verifyBlackSugarMix(InOrder inOrder) {
		verifyBlackMix(inOrder);
		inOrder.verify(sugarDispenser).release(5);
	}

	@Test
	public void drinkAndCancel() {
		// Preparing scenario: first drink
		validSession(Drink.BLACK_SUGAR, Coin.dime, Coin.quarter);

		// Preparing scenario: before cancel
		insertCoins(Coin.dollar);
		InOrder inOrder = resetMocks();

		// Operation under test
		facade.cancel();

		// Verification
		verifyCancel(inOrder, Coin.dollar);
	}

	protected void verifyCancel(InOrder inOrder, Coin... change) {
		verifyCancelMessage(inOrder);
		verifyReleaseCoins(inOrder, change);
		verifyNewSession(inOrder);
	}

	@Test
	public void cancelAndDrink() {
		// Preparing scenario: cancel
		facade = createFacade(factory);
		insertCoins(Coin.halfDollar);
		facade.cancel();

		// Preparing scenario: before select
		insertCoins(Coin.dime, Coin.quarter);
		InOrder inOrder = resetMocks();

		// Simulating returns
		doContainBlackIngredients();

		// Operation under test
		facade.select(Drink.BLACK);

		// Verification
		verifyBlackPlan(inOrder);
		verifyBlackMix(inOrder);
		verifyDrinkRelease(inOrder);
		verifyNewSession(inOrder);
	}

	@Test
	public void selectBlackWithoutCoffeePowder() {
		InOrder inOrder = prepareScenarioWithCoins(Coin.quarter, Coin.dime);

		// Simulating returns
		doNotContain(coffeePowderDispenser, anyDouble()); // Out of Coffee
															// powder!
		doContain(waterDispenser, anyDouble());
		doContain(cupDispenser, 1);

		// Operation under test
		facade.select(Drink.BLACK);

		// Verification
		inOrder.verify(cupDispenser).contains(1);
		inOrder.verify(waterDispenser).contains(anyDouble());
		inOrder.verify(coffeePowderDispenser).contains(anyDouble());
		verifyOutOfIngredient(inOrder, Messages.OUT_OF_COFFEE_POWDER,
				Coin.quarter, Coin.dime);
	}

	@Test
	public void selectBlackWithoutSugar() {
		InOrder inOrder = prepareScenarioWithCoins(Coin.halfDollar);

		// Simulating returns
		doContain(coffeePowderDispenser, anyDouble());
		doContain(waterDispenser, anyDouble());
		doContain(cupDispenser, 1);
		doNotContain(sugarDispenser, anyDouble()); // Out of Sugar

		// Operation under test
		facade.select(Drink.BLACK_SUGAR);

		// Verification
		inOrder.verify(cupDispenser).contains(1);
		inOrder.verify(waterDispenser).contains(anyDouble());
		inOrder.verify(coffeePowderDispenser).contains(anyDouble());
		inOrder.verify(sugarDispenser).contains(anyDouble());
		verifyOutOfIngredient(inOrder, Messages.OUT_OF_SUGAR, Coin.halfDollar);
	}

	@Test
	public void selectBlackWithoutWater() {
		InOrder inOrder = prepareScenarioWithCoins(Coin.quarter, Coin.dime);

		// Simulating returns
		doNotContain(waterDispenser, anyDouble()); // Out of Water
		doContain(cupDispenser, 1);

		// Operation under test
		facade.select(Drink.BLACK);

		// Verification
		inOrder.verify(cupDispenser).contains(1);
		inOrder.verify(waterDispenser).contains(anyDouble());
		verifyOutOfIngredient(inOrder, Messages.OUT_OF_WATER, Coin.quarter,
				Coin.dime);
	}

	@Test
	public void selectBlackWithoutCup() {
		InOrder inOrder = prepareScenarioWithCoins(Coin.quarter, Coin.dime);

		// Simulating returns
		doNotContain(cupDispenser, 1); // Out of Cup

		// Operation under test
		facade.select(Drink.BLACK_SUGAR);

		// Verification
		inOrder.verify(cupDispenser).contains(1);
		verifyOutOfIngredient(inOrder, Messages.OUT_OF_CUP, Coin.quarter,
				Coin.dime);
	}

	@Test
	public void selectWhiteWithoutChange() {
		InOrder inOrder = prepareScenarioWithCoins(Coin.quarter, Coin.dime);

		// Simulating returns
		doContainWhiteIngredients();

		// Operation under test
		facade.select(Drink.WHITE);

		// Verification
		verifyWhitePlan(inOrder);
		verifyWhiteMix(inOrder);
		verifyDrinkRelease(inOrder);
		verifyNewSession(inOrder);
	}

	@Test
	public void selectWhiteSugarWithChange() {
		InOrder inOrder = prepareScenarioWithCoins(Coin.halfDollar);

		// Simulating returns
		doCount(Coin.dime, 10);
		doCount(Coin.nickel, 10);
		doContainWhiteSugarIngredients();

		// Operation under test
		facade.select(Drink.WHITE_SUGAR);

		// Verification
		verifyWhiteSugarPlan(inOrder);
		verifyCount(inOrder, Coin.dime, Coin.nickel);
		verifyWhiteSugarMix(inOrder);
		verifyDrinkRelease(inOrder);
		verifyCloseSession(inOrder, Coin.dime, Coin.nickel);
	}

	@Test
	public void selectBlackWithoutMoney() {
		facade = createFacade(factory);
		InOrder inOrder = resetMocks();

		// Simulating returns
		doContainBlackIngredients();

		// Operation under test
		facade.select(Drink.BLACK);

		// Verification
		inOrder.verify(display).warn(Messages.NO_ENOUGHT_MONEY);
		verifyCloseSession(inOrder);

		// Preparing scenario: New session
		insertCoins(Coin.dime);
		inOrder = resetMocks();

		// Simulating returns
		doContainBlackIngredients();

		// Operation under test
		facade.select(Drink.BLACK);

		// Verification
		inOrder.verify(display).warn(Messages.NO_ENOUGHT_MONEY);
		verifyCloseSession(inOrder, Coin.dime);
	}

	@Test
	public void selectBlackWithoutEnoughtChange() {
		InOrder inOrder = prepareScenarioWithCoins(Coin.halfDollar);

		// Simulating returns
		doCount(Coin.dime, 0);
		doCount(Coin.nickel, 0);
		doCount(Coin.penny, 0); // Out of change
		doContainWhiteIngredients();

		// Operation under test
		facade.select(Drink.WHITE);

		// Verification
		verifyWhitePlan(inOrder);
		verifyCount(inOrder, Coin.dime, Coin.nickel, Coin.penny);
		inOrder.verify(display).warn(Messages.NO_ENOUGHT_CHANGE);
		verifyCloseSession(inOrder, Coin.halfDollar);
	}

	@Test
	public void selectWhiteWithNonTrivialChange() {
		InOrder inOrder = prepareScenarioWithCoins(Coin.halfDollar);

		// Simulating returns
		doCount(Coin.dime, 0);
		doCount(Coin.nickel, 10);
		doContainWhiteIngredients();

		// Operation under test
		facade.select(Drink.WHITE);

		// Verification
		verifyWhitePlan(inOrder);
		verifyCount(inOrder, Coin.dime, Coin.nickel);
		verifyWhiteMix(inOrder);
		verifyDrinkRelease(inOrder);

		inOrder.verify(cashBox, times(3)).release(Coin.nickel);
		verifyNewSession(inOrder);
	}

	@Test
	public void selectWhiteWithoutCreamer() {
		InOrder inOrder = prepareScenarioWithCoins(Coin.dollar);

		// Simulating returns
		doContain(coffeePowderDispenser, anyDouble());
		doContain(waterDispenser, anyDouble());
		doContain(cupDispenser, 1);
		doNotContain(creamerDispenser, anyDouble()); // Out of Creamer!

		// Operation under test
		facade.select(Drink.WHITE);

		// Verification
		inOrder.verify(cupDispenser).contains(1);
		inOrder.verify(waterDispenser).contains(anyDouble());
		inOrder.verify(coffeePowderDispenser).contains(anyDouble());
		inOrder.verify(creamerDispenser).contains(anyDouble());
		verifyOutOfIngredient(inOrder, Messages.OUT_OF_CREAMER, Coin.dollar);
	}

	@Test
	public void blackIngredientsQuantities() {
		InOrder inOrder = prepareScenarioWithCoins(Coin.quarter, Coin.dime);

		// Simulating returns
		doContainBlackIngredients();

		// Operation under test
		facade.select(Drink.BLACK);

		// Verification
		verifyBlackPlan(inOrder);
		verifyBlackMix(inOrder);
		verifyDrinkRelease(inOrder);
		verifyNewSession(inOrder);
	}

	@Test
	public void blackSugarIngredientsQuantities() {
		InOrder inOrder = prepareScenarioWithCoins(Coin.quarter, Coin.dime);

		// Simulating returns
		doContainBlackSugarIngredients();

		// Operation under test
		facade.select(Drink.BLACK_SUGAR);

		// Verification
		verifyBlackSugarPlan(inOrder);
		verifyBlackSugarMix(inOrder);
		verifyDrinkRelease(inOrder);
		verifyNewSession(inOrder);
	}

	@Test
	public void whiteIngredientsQuantities() {
		InOrder inOrder = prepareScenarioWithCoins(Coin.quarter, Coin.dime);

		// Simulating returns
		doContainWhiteIngredients();

		// Operation under test
		facade.select(Drink.WHITE);

		// Verification
		verifyWhitePlan(inOrder);
		verifyWhiteMix(inOrder);
		verifyDrinkRelease(inOrder);
		verifyNewSession(inOrder);
	}

	@Test
	public void whiteSugarIngredientsQuantities() {
		InOrder inOrder = prepareScenarioWithCoins(Coin.quarter, Coin.dime);

		// Simulating returns
		doContainWhiteSugarIngredients();

		// Operation under test
		facade.select(Drink.WHITE_SUGAR);

		// Verification
		verifyWhiteSugarPlan(inOrder);
		verifyWhiteSugarMix(inOrder);
		verifyDrinkRelease(inOrder);
		verifyNewSession(inOrder);
	}

	@Test
	public void selectBouillonWithoutChange() {
		InOrder inOrder = prepareScenarioWithCoins(Coin.quarter);

		// Simulating returns
		doContain(bouillonDispenser, anyDouble());
		doContain(waterDispenser, anyDouble());
		doContain(cupDispenser, 1);

		// Operation under test
		facade.select(Drink.BOUILLON);

		// Verification
		verifyBouillonPlan(inOrder);
		verifyBouillonMix(inOrder);
		verifyDrinkRelease(inOrder);
		verifyNewSession(inOrder);
	}

	protected void verifyBouillonPlan(InOrder inOrder) {
		inOrder.verify(cupDispenser).contains(1);
		inOrder.verify(waterDispenser).contains(100);
		inOrder.verify(bouillonDispenser).contains(10);
	}

	protected void verifyBouillonMix(InOrder inOrder) {
		inOrder.verify(display).info(Messages.MIXING);
		inOrder.verify(bouillonDispenser).release(10);
		inOrder.verify(waterDispenser).release(100);
	}

	protected void doCount(Coin coin, int amount) {
		when(cashBox.count(coin)).thenReturn(amount);
	}

	protected void doContainWhiteSugarIngredients() {
		doContainWhiteIngredients();
		doContain(sugarDispenser, anyDouble());
	}

	protected void verifyWhiteSugarPlan(InOrder inOrder) {
		verifyWhitePlan(inOrder);
		inOrder.verify(sugarDispenser).contains(5);
	}

	protected void verifyWhiteSugarMix(InOrder inOrder) {
		verifyWhiteMix(inOrder);
		inOrder.verify(sugarDispenser).release(5);
	}

	protected void verifyCount(InOrder inOrder, Coin... change) {
		for (Coin coin : change) {
			inOrder.verify(cashBox).count(coin);
		}
	}

	protected void verifyCloseSession(InOrder inOrder, Coin... change) {
		verifyReleaseCoins(inOrder, change);
		verifyNewSession(inOrder);
	}

	protected void doContainWhiteIngredients() {
		doContainBlackIngredients();
		doContain(creamerDispenser, anyDouble());
	}

	protected void doContainBouillonIngredients() {
		doContain(bouillonDispenser, anyDouble());
		doContain(waterDispenser, anyDouble());
		doContain(cupDispenser, 1);
	}

	protected void verifyWhitePlan(InOrder inOrder) {
		inOrder.verify(cupDispenser).contains(1);
		inOrder.verify(waterDispenser).contains(80);
		inOrder.verify(coffeePowderDispenser).contains(15);
		inOrder.verify(creamerDispenser).contains(20);
	}

	protected void verifyWhiteMix(InOrder inOrder) {
		inOrder.verify(display).info(Messages.MIXING);
		inOrder.verify(coffeePowderDispenser).release(15);
		inOrder.verify(waterDispenser).release(80);
		inOrder.verify(creamerDispenser).release(20);
	}

	protected void verifyOutOfIngredient(InOrder inOrder, String message,
			Coin... coins) {
		inOrder.verify(display).warn(message);
		verifyReleaseCoins(inOrder, coins);
		verifyNewSession(inOrder);
	}

	protected void doContain(Dispenser dispenser, Object amount) {
		when(dispenser.contains(amount)).thenReturn(true);
	}

	protected void doNotContain(Dispenser dispenser, Object amount) {
		when(dispenser.contains(amount)).thenReturn(false);
	}

	protected void verifyDrinkRelease(InOrder inOrder) {
		inOrder.verify(display).info(Messages.RELEASING);
		inOrder.verify(cupDispenser).release(1);
		inOrder.verify(drinkDispenser).release(100);
		inOrder.verify(display).info(Messages.TAKE_DRINK);
	}

	protected void verifyCancelMessage(InOrder inOrder) {
		inOrder.verify(display).warn(Messages.CANCEL);
	}

	protected void insertCoins(Coin... coins) {
		for (Coin coin : coins) {
			facade.insertCoin(coin);
		}
	}

	protected void verifyReleaseCoins(InOrder inOrder, Coin coin, int times) {
		inOrder.verify(cashBox, times(times)).release(coin);
	}

	protected void verifyReleaseCoins(InOrder inOrder, Coin... coins) {
		for (Coin coin : coins) {
			inOrder.verify(cashBox).release(coin);
		}
	}

	protected InOrder resetMocks() {
		reset(mocks());
		return inOrder(mocks());
	}

	protected Object[] mocks() {
		return asArray(display, cashBox, coffeePowderDispenser, waterDispenser,
				cupDispenser, drinkDispenser, sugarDispenser, creamerDispenser,
				bouillonDispenser);
	}

	protected Object[] asArray(Object... objs) {
		return objs;
	}

	protected void verifyBadgeRead() {
		verify(display).info(Messages.BADGE_READ);
	}
}
