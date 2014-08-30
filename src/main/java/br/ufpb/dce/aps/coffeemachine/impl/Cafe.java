package br.ufpb.dce.aps.coffeemachine.impl;

import br.ufpb.dce.aps.coffeemachine.Button;
import net.compor.frameworks.jcf.api.Component;
import net.compor.frameworks.jcf.api.Service;

public class Cafe extends Component {

	public Cafe() {
		super("Cafe");
	}

	@Service
	public boolean VerificaDrink(Button button) {
		boolean notAlerta = false;
		if (button.equals(Button.BUTTON_1)) {
			notAlerta = (Boolean) requestService("VerificaDrinkPreto");
		}

		if (button.equals(Button.BUTTON_2)) {
			notAlerta = (Boolean) requestService("VerificaDrinkBranco");
		}

		if (button.equals(Button.BUTTON_4)) {
			notAlerta = (Boolean) requestService("VerificaDrinkBrancoComAcucar");
		}

		if (button.equals(Button.BUTTON_3)) {
			notAlerta = (Boolean) requestService("VerificaDrinkPretoComAcucar");
		}
		if (button.equals(Button.BUTTON_5)) {
			notAlerta = (Boolean) requestService("VerificaDrinkBouillon");
		}
		return notAlerta;
	}

	@Service
	public void comparaDrink(Button button) {
		if (button.equals(Button.BUTTON_1)) {
			requestService("liberaIngredientesCafePreto");
		}

		if (button.equals(Button.BUTTON_3)) {
			requestService("LiberaIngredientesCafePretoComAcucar");
		}
		if (button.equals(Button.BUTTON_2)) {
			requestService("liberaIngredientesCafeBranco");
		}

		if (button.equals(Button.BUTTON_4)) {
			requestService("liberaIngredientesCafeBrancoComAcucar");
		}
		if (button.equals(Button.BUTTON_5)) {
			requestService("liberaIngredientesBouillon");
		}
	}
}
