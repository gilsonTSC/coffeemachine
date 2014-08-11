package br.ufpb.dce.aps.coffeemachine.impl;

import java.util.ArrayList;





import net.compor.frameworks.jcf.api.ComporFacade;
import br.ufpb.dce.aps.coffeemachine.CoffeeMachine;
import br.ufpb.dce.aps.coffeemachine.CoffeeMachineException;
import br.ufpb.dce.aps.coffeemachine.Coin;
import br.ufpb.dce.aps.coffeemachine.ComponentsFactory;
import br.ufpb.dce.aps.coffeemachine.Drink;
import br.ufpb.dce.aps.coffeemachine.Messages;

public class MyCoffeeMachine extends ComporFacade implements CoffeeMachine {


	private ComponentsFactory factory;
	private int dolares, centavos;
	private ArrayList<Coin> moedas;
	private final int  VALORCAFE = 35;
	boolean notAlerta= true;

	public MyCoffeeMachine(ComponentsFactory factory) {
		this.factory = factory;
		this.factory.getDisplay().info("Insert coins and select a drink!");
		this.dolares = 0;
		this.centavos = 0;
		this.moedas = new ArrayList<Coin>();
		this.addComponents();
		
	}
	@Override
	protected void addComponents() {
		this.add(new CafePreto(this.factory));
		this.add(new CafeBranco(this.factory));
		this.add(new CafeBrancoComAcucar(this.factory));
		this.add(new CafePretoComAcucar(this.factory));
	}

	public void insertCoin(Coin coin) {
		if (coin == null) {
			throw new CoffeeMachineException("Coin null");
		}
		this.moedas.add(coin);
		this.dolares += coin.getValue() / 100;
		this.centavos += coin.getValue() % 100;
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
		for (Coin r : Coin.reverse()) {
			for(Coin aux : this.moedas){
				if(aux == r){
					this.factory.getCashBox().release(aux);
				}
			}
		}
				
		this.zeraVecto();
		this.factory.getDisplay().info(Messages.INSERT_COINS);
	}

	private void zeraVecto() {
		this.moedas.clear();
	}
	
	private void planCoins(int troco) {
		for (Coin r : Coin.reverse()) {
			while (r.getValue() <= troco) {
				if(this.factory.getCashBox().count(r) == 0){
					this.factory.getDisplay().warn(Messages.NO_ENOUGHT_MONEY);
					this.returnCoin();	
				}
				troco -= r.getValue();
			}
		}
	}
	
	private void releaseCoins(int troco) {		
		for (Coin r : Coin.reverse()) {
			while (r.getValue() <= troco) {
				this.factory.getCashBox().release(r);
				troco -= r.getValue();
			}
		}

	}

	private int calculaTroco(){
		int contadorMoedas = 0;
		
		for(Coin r : Coin.reverse()){
			for(Coin aux : this.moedas){
				if(aux == r){
					contadorMoedas += aux.getValue();
				}
			}
		}
		return contadorMoedas - this.VALORCAFE;
	}


	public void select(Drink drink) {
				
		if(calculaTroco()<0){
			this.factory.getDisplay().warn(Messages.NO_ENOUGHT_MONEY);
			this.returnCoin();		
			return;
		}
		notAlerta = (Boolean)requestService("VerificaDrink", drink);
		
		if(!notAlerta){
			returnCoin();
			return;
		}
		
		planCoins(calculaTroco());
		
		this.factory.getDisplay().info(Messages.MIXING);
		requestService("comparaDrink", drink);
		requestService("LiberandoBebida");
		releaseCoins(calculaTroco());
	
		this.zeraVecto();
		this.factory.getDisplay().info(Messages.INSERT_COINS);
	}
}