package ruleml.translator;

public class TestDataModel {

	public static class Keep {
		private String keeper;
		private String item;

		public Keep(String item, String keeper) {
			this.keeper = keeper;
			this.item = item;
		}

		public void setKeeper(String keeper) {
			this.keeper = keeper;
		}

		public String getKeeper() {
			return keeper;
		}

		public void setItem(String item) {
			this.item = item;
		}

		public String getItem() {
			return item;
		}
	}

	public static class Own {
		private String owner;
		private String item;

		public Own(String item, String owner) {
			this.owner = owner;
			this.item = item;
		}

		public void setOwner(String owner) {
			this.owner = owner;
		}

		public String getOwner() {
			return owner;
		}

		public void setItem(String item) {
			this.item = item;
		}

		public String getItem() {
			return item;
		}
	}

	public static class Buy {
		private String buyer;
		private String seller;
		private String item;

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((buyer == null) ? 0 : buyer.hashCode());
			result = prime * result + ((item == null) ? 0 : item.hashCode());
			result = prime * result
					+ ((seller == null) ? 0 : seller.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Buy other = (Buy) obj;
			if (buyer == null) {
				if (other.buyer != null)
					return false;
			} else if (!buyer.equals(other.buyer))
				return false;
			if (item == null) {
				if (other.item != null)
					return false;
			} else if (!item.equals(other.item))
				return false;
			if (seller == null) {
				if (other.seller != null)
					return false;
			} else if (!seller.equals(other.seller))
				return false;
			return true;
		}

		public Buy(String buyer, String item, String seller) {
			this.setSeller(seller);
			this.setBuyer(buyer);
			this.setItem(item);
		}

		public void setSeller(String seller) {
			this.seller = seller;
		}

		public String getSeller() {
			return seller;
		}

		public void setBuyer(String buyer) {
			this.buyer = buyer;
		}

		public String getBuyer() {
			return buyer;
		}

		public void setItem(String item) {
			this.item = item;
		}

		public String getItem() {
			return item;
		}
	}

	public static class Sell {

		private Person seller;
		private Person buyer;
		private String item;

		public Sell(Person buyer, String item, Person seller) {
			this.seller = seller;
			this.buyer = buyer;
			this.item = item;
		}

		public void setSeller(Person seller) {
			this.seller = seller;
		}

		public Person getSeller() {
			return seller;
		}

		public void setByuer(Person buyer) {
			this.buyer = buyer;
		}

		public Person getBuyer() {
			return buyer;
		}

		public void setItem(String item) {
			this.item = item;
		}

		public String getItem() {
			return item;
		}
	}

	public static class Person {
		private String name;
		private int age;

		public Person(int age, String name) {
			this.name = name;
			this.age = age;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getAge() {
			return age;
		}

		public void setAge(int age) {
			this.age = age;
		}
	}

	public static class Likes {
		private String person;
		private String object;

		public String getPerson() {
			return person;
		}

		public void setPerson(String person) {
			this.person = person;
		}

		public String getObject() {
			return object;
		}

		public void setObject(String object) {
			this.object = object;
		}
	}
}
