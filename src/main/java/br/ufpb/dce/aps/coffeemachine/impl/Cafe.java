package br.ufpb.dce.aps.coffeemachine.impl;

import br.ufpb.dce.aps.coffeemachine.Drink;
import net.compor.frameworks.jcf.api.Component;
import net.compor.frameworks.jcf.api.Service;

public class Cafe extends Component{

	public Cafe() {
		super("Cafe");
	}

	@Service
	public boolean VerificaDrink(Drink drink){
		boolean notAlerta = false;
		if (drink.equals(Drink.BLACK)) {			
			notAlerta = (Boolean)requestService("VerificaDrinkPreto");
		}
		
		if (drink.equals(Drink.WHITE)) {
			notAlerta = (Boolean)requestService("VerificaDrinkBranco");
		}
		
		if (drink.equals(Drink.WHITE_SUGAR)) {
			notAlerta = (Boolean) requestService("VerificaDrinkBrancoComAcucar");
		}

		if (drink.equals(Drink.BLACK_SUGAR)) {
			notAlerta = (Boolean)requestService("VerificaDrinkPretoComAcucar");
		}
		if(drink.equals(Drink.BOUILLON)){
			notAlerta = (Boolean)requestService("VerificaDrinkBouillon");
		}
		return notAlerta;
	}
	
	@Service
	public void comparaDrink(Drink drink){
		if (drink.equals(Drink.BLACK)) {
			requestService("liberaIngredientesCafePreto");
		}

		if (drink.equals(Drink.BLACK_SUGAR)) {
			requestService("LiberaIngredientesCafePretoComAcucar");
		}
		if (drink.equals(Drink.WHITE)) {
			requestService("liberaIngredientesCafeBranco");
		}
		
		if (drink.equals(Drink.WHITE_SUGAR)) {
			requestService("liberaIngredientesCafeBrancoComAcucar");
		}
		if (drink.equals(Drink.BOUILLON)) {
			requestService("liberaIngredientesBouillon");
		}	
	}
}
