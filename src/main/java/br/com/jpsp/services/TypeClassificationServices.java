package br.com.jpsp.services;

import java.util.Set;

import br.com.jpsp.dao.TypeClassificationDAO;
import br.com.jpsp.model.TypeClassification;

public class TypeClassificationServices implements CRUDServices<TypeClassification> {


	public static final TypeClassificationServices instance = new TypeClassificationServices();
	
	private final TypeClassificationDAO dao = TypeClassificationDAO.instance;
	
	private TypeClassificationServices() {
		super();
	}
	
	@Override
	public void add(TypeClassification typeClass) {
		dao.addTypeClassification(typeClass);
	}

	@Override
	public void remove(TypeClassification typeClass) throws Exception {
		if (typeClass.isBlocked()) {
			throw new Exception(Strings.jPSP.ERROR_BLOCKED.replaceAll("&1", Strings.jPSP.TASK_CLASS));
		} else 
			this.dao.removeTypeClassification(typeClass);
	}

	@Override
	public void update(TypeClassification typeClass) throws Exception {
		if (typeClass.isBlocked()) {
			throw new Exception(Strings.jPSP.ERROR_BLOCKED.replaceAll("&1", Strings.jPSP.TASK_CLASS));
		} else 
			this.dao.updateTypeClassification(typeClass);
		
	}

	@Override
	public Set<TypeClassification> getAll() {
		return dao.getAllTypeClassification();
	}

}
