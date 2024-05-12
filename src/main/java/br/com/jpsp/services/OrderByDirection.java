package br.com.jpsp.services;

public enum OrderByDirection {
	ASC(0, "Ascendente (mais antigo para mais recente)"),
	DESC(1, "Descendente (mais recente para mais antigo)");

	private int id;
	private String description;

	OrderByDirection() {}
	OrderByDirection(int id, String description) {
		this.id = id;
		this.description = description;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public String toString() {
		return this.description;
	}

	public boolean equals(OrderByDirection that) {
		return this.id == that.id && this.description.equals(that.description);
	}

	public boolean isDESC() {
		return DESC.equals(this);
	}

	public boolean isASC() {
		return ASC.equals(this);
	}

	public String getDirection() {
		String order = "ASC";
		switch (this) {
			case ASC:
				order = "ASC";
				break;
			case DESC:
				order = "DESC";
				break;
			default:
				break;

		}

		return order;
	}
}
