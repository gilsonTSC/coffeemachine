package br.ufpb.dce.aps.coffeemachine.impl;

import br.ufpb.dce.aps.coffeemachine.ComponentsFactory;

public class CafeBrancoComAcucar extends CafePreto {
	
	public CafeBrancoComAcucar(ComponentsFactory factory){
		super(factory);
	}

	public boolean VerificaDrinkBrancoComAcucar(){
		if(!VerificaDrinkPreto()){
			return false;
		}
		factory.getCreamerDispenser().contains(2.0); 
		factory.getSugarDispenser().contains(5.0);
		return true;
		
	}
	
	public void liberaIngredientesCafeBrancoComAcucar(){
		liberaIngredientesCafePreto();
		this.factory.getCreamerDispenser().release(1.2);
		factory.getSugarDispenser().release(5.0);
	}
}
