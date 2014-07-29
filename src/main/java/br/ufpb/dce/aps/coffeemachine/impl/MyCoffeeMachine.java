package br.ufpb.dce.aps.coffeemachine.impl;

import static org.mockito.Matchers.anyDouble;
import net.compor.frameworks.jcf.api.ComporFacade;
import br.ufpb.dce.aps.coffeemachine.CoffeeMachine;
import br.ufpb.dce.aps.coffeemachine.CoffeeMachineException;
import br.ufpb.dce.aps.coffeemachine.Coin;
import br.ufpb.dce.aps.coffeemachine.ComponentsFactory;
import br.ufpb.dce.aps.coffeemachine.Drink;
import br.ufpb.dce.aps.coffeemachine.Messages;

public class MyCoffeeMachine extends ComporFacade implements CoffeeMachine {

	private ComponentsFactory factory;
	private int dolares, centavos, cont;
	private Coin[] moedas;
	private Drink drink;
	private int quantDrink = 0;

	public MyCoffeeMachine(ComponentsFactory factory) {
		this.factory = factory;
		this.factory.getDisplay().info("Insert coins and select a drink!");
		this.dolares = 0;
		this.centavos = 0;
		this.moedas = new Coin[100];
		this.cont = 0;
	}

	public void insertCoin(Coin dime) {
		if (dime == null) {
			throw new CoffeeMachineException("Coin null");
		}
		this.moedas[++this.cont] = dime;
		this.dolares += dime.getValue() / 100;
		this.centavos += dime.getValue() % 100;
		this.factory.getDisplay().info(
				"Total: US$ " + this.dolares + "." + this.centavos);
	}

	public void cancel() {
		if (this.dolares == 0 && this.centavos == 0) {
			throw new CoffeeMachineException("Não tem moedas inseridas");
		}
		this.factory.getDisplay().warn(Messages.CANCEL);
		this.returnCoin();
	}

	private void returnCoin() {
		Coin[] c = Coin.reverse();
		for (int i = 0; i < c.length; i++) {
			for (int j = 0; j < this.moedas.length; j++) {
				if (c[i].equals(this.moedas[j])) {
					this.factory.getCashBox().release(this.moedas[j]);
				}
			}
		}
		this.zeraVecto();
		this.factory.getDisplay().info(Messages.INSERT_COINS);
	}
	
	private void zeraVecto(){
		for (int j = 0; j < this.moedas.length; j++) {
			this.moedas[j] = null;
		}
	}

	public void select(Drink drink) {

		if (!this.factory.getCupDispenser().contains(1)) {
			this.factory.getDisplay().warn(Messages.OUT_OF_CUP);
			this.returnCoin();
			return;
		}
		if (!this.factory.getWaterDispenser().contains(1.2)) {
			this.factory.getDisplay().warn(Messages.OUT_OF_WATER);
			this.returnCoin();
			return;
		}
		if (!this.factory.getCoffeePowderDispenser().contains(1.2)) {
			this.factory.getDisplay().warn(Messages.OUT_OF_COFFEE_POWDER);
			this.returnCoin();
			return;
		}

		if (drink.equals(Drink.BLACK_SUGAR)) {
			if (!this.factory.getSugarDispenser().contains(1.2)) {
				this.factory.getDisplay().warn(Messages.OUT_OF_SUGAR);
				this.returnCoin();
				return;
			}
		}
		if (drink.equals(Drink.WHITE)) {
			this.factory.getCreamerDispenser().contains(1.2);
		}
		this.factory.getDisplay().info(Messages.MIXING);
		this.factory.getCoffeePowderDispenser().release(1.2);
		this.factory.getWaterDispenser().release(1.2);

		if (drink.equals(Drink.BLACK_SUGAR)) {
			this.factory.getSugarDispenser().release(1.2);
		}
		if (drink.equals(Drink.WHITE)) {
			this.factory.getCreamerDispenser().release(1.2);
		}
		
		
		
		this.factory.getDisplay().info(Messages.RELEASING);
		this.factory.getCupDispenser().release(1);
		this.factory.getDrinkDispenser().release(1.2);
		this.factory.getDisplay().info(Messages.TAKE_DRINK);

		this.zeraVecto();
		this.factory.getDisplay().info(Messages.INSERT_COINS);
	}
	
	private void calculaTroco(int troco){
		Coin[] c = Coin.reverse();
		for (int i = 0; i < c.length; i++) {
			if(c[i].getValue() <= troco){
				this.factory.getCashBox().count(c[i]);
			}
		}
	}
}