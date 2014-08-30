package br.ufpb.dce.aps.coffeemachine.impl;

import java.util.ArrayList;
import java.util.HashMap;

import net.compor.frameworks.jcf.api.Component;
import net.compor.frameworks.jcf.api.Service;
import br.ufpb.dce.aps.coffeemachine.Button;
import br.ufpb.dce.aps.coffeemachine.CoffeeMachineException;
import br.ufpb.dce.aps.coffeemachine.Coin;
import br.ufpb.dce.aps.coffeemachine.ComponentsFactory;
import br.ufpb.dce.aps.coffeemachine.Messages;

public class MyCoin extends Component{

	private ComponentsFactory factory;
	private int dolares, centavos;
	private ArrayList<Coin> moedas;
	private int VALORCAFE = 35;
	int[] trocoPlan = new int[6];
	private static HashMap<Button, Integer> valorDrink = new HashMap<Button, Integer>();
	
	public MyCoin(ComponentsFactory factory){
		super("Classe moedas");
		this.factory = factory;
		this.dolares = 0;
		this.centavos = 0;
		this.moedas = new ArrayList<Coin>();
		//this.valorDrink = new HashMap<Button, Integer>();
		this.addValores();
	}
	@Service
	public void addCoin(Coin coin){
		this.moedas.add(coin);
	}
	@Service
	public void cancel() {
		if (this.dolares == 0 && this.centavos == 0) {
			throw new CoffeeMachineException("Não tem moedas inseridas");
		}
		this.factory.getDisplay().warn(Messages.CANCEL);
		this.returnCoin();
	}
	@Service
	public void returnCoin() {
		for (Coin r : Coin.reverse()) {
			for (Coin aux : this.moedas) {
				if (aux == r) {
					this.factory.getCashBox().release(aux);
				}
			}
		}
		this.zeraVecto();
		this.factory.getDisplay().info(Messages.INSERT_COINS);
	}
	@Service
	public void releaseCoins(int[] quantCoin) {

		for (int i = 0; i < quantCoin.length; i++) {
			int count = quantCoin[i];
			Coin coin = Coin.reverse()[i];

			for (int j = 1; j <= count; j++) {
				this.factory.getCashBox().release(coin);
			}
		}
	}
	@Service
	public int[] planCoins(int troco) throws CoffeeMachineException {
		int[] trocoPlan = new int[6];
		int i = 0;
		for (Coin r : Coin.reverse()) {
			if (r.getValue() <= troco && factory.getCashBox().count(r) > 0) {
				while (r.getValue() <= troco) {
					troco -= r.getValue();
					trocoPlan[i]++;
				}
			}
			i++;
		}
		if (troco != 0) {
			throw new CoffeeMachineException("");
		}
		return trocoPlan;
	}
	@Service
	public int calculaTroco() {
		int contadorMoedas = 0;
		for (Coin r : Coin.reverse()) {
			for (Coin aux : this.moedas) {
				if (aux == r) {
					contadorMoedas += aux.getValue();
				}
			}
		}
		return contadorMoedas - this.VALORCAFE;
	}
	
	@Service
	public void setVALORCAFE(int valor){
		this.VALORCAFE = valor;
	}
	@Service
	public void zeraVecto() {
		this.moedas.clear();
	}
	@Service
	public int dolares(Coin coin) {
		return this.dolares += coin.getValue() / 100;
	}
	@Service
	public int centavos(Coin coin) {
		return this.centavos += coin.getValue() % 100;
	}
	@Service
	public int getValorCafe() {
		return this.VALORCAFE;
	}
	@Service
	public static void put(Button button, int priceCents){
		valorDrink.put(button, priceCents);
	}
	private static void addValores(){
		valorDrink.put(Button.BUTTON_1, 35);
		valorDrink.put(Button.BUTTON_2, 35);
		valorDrink.put(Button.BUTTON_3, 35);
		valorDrink.put(Button.BUTTON_4, 35);
		valorDrink.put(Button.BUTTON_5, 25);
	}
	@Service
	public static int get(Button button){
		return valorDrink.get(button);
	}
}
