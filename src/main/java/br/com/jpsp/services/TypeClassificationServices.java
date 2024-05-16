package br.com.jpsp.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import br.com.jpsp.model.TypeClassification;
import br.com.jpsp.repository.TypeClassificationDAO;
import br.com.jpsp.utils.Utils;

/**
 * 
 * @author kleber
 *
 */
public class TypeClassificationServices extends RepositoryAccessServices implements CRUDServices<TypeClassification> {


	public static final TypeClassificationServices instance = new TypeClassificationServices();
	
	private final TypeClassificationDAO dao = TypeClassificationDAO.instance;
	
	private TypeClassificationServices() {
		super();
	}
	
	@Override
	public void add(TypeClassification typeClass) {
		synchronized (TypeClassificationServices.this) {
			dao.add(typeClass);
		}
	}

	@Override
	public void remove(TypeClassification typeClass) throws Exception {
		if (typeClass.isBlocked()) {
			throw new Exception(Strings.jPSP.ERROR_BLOCKED.replaceAll("&1", Strings.jPSP.TASK_CLASS));
		} else {
			synchronized (TypeClassificationServices.this) {
				this.dao.remove(typeClass);
			}
		}
	}

	@Override
	public void update(TypeClassification typeClass) throws Exception {
		if (typeClass.isBlocked()) {
			throw new Exception(Strings.jPSP.ERROR_BLOCKED.replaceAll("&1", Strings.jPSP.TASK_CLASS));
		} else {
			synchronized (TypeClassificationServices.this) {
				this.dao.update(typeClass);
			}
		}
		
	}

	@Override
	public Set<TypeClassification> getAll() {
		return dao.getAll();
	}
	
	public List<String> getAllTypeClassDesc() {
		Set<TypeClassification> allTypeClass = this.dao.getAllCachedTypeClassification();
		List<String> allTypeClassDescriptions = new ArrayList<String>();
		if (!Utils.isEmpty(allTypeClass)) {
			for (TypeClassification type : allTypeClass) {
				allTypeClassDescriptions.add(type.getDescription());
			}
		}
		return allTypeClassDescriptions;
	}

	public void addTypeClasses(Set<String> typeClass) {
		if (!Utils.isEmpty(typeClass)) {
			typeClass.forEach(tc -> {
				TypeClassification newTypeClass = new TypeClassification().from(tc);
				if (classDAO.exists(newTypeClass)) {
					classDAO.add(newTypeClass);
				}
			});
		}
	}

}
