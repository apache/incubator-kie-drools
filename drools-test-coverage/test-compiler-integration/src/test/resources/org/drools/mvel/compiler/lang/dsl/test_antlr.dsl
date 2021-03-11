#This is a sample DSL for a ficticous E-Commerce website that is building a recommendation engine


[condition][]address is present where {constraints}=u : User() and exists (a: Address( where {constraints}) from u.addresses)
[condition][]where {attr:[A-Za-z0-9]+} is "{value}"= {attr} == "{value}" 
[condition][]and {attr:[A-Za-z0-9]+} is "{value}"= , {attr} == "{value}"
#[keyword][*]regra {atributos} faça {rhs} se {lhs} fim=rule {atributos} \\n when\\n    {lhs}\\n then\\n    {rhs}\\n end
#[keyword][*]consulta=query
#[keyword][]fim=end
#[when][woolfel.ecommerce.model.Customer]the Customer=cust : Customer()
#[when][woolfel.ecommerce.model.Customer]- has an email=emailAddress != null
#[when][woolfel.ecommerce.model.Customer]- first name is "{first}"=first == "{first}"
#[when][woolfel.ecommerce.model.Customer]- last name is "{surname}"=sname: surname == "{surname}"
#[when][woolfel.ecommerce.model.CustomerProfile]a User profile=userprof : CustomerProfile(prfid : profileId)
#[when][woolfel.ecommerce.model.Response]the Response=resp : Response()
#[when][woolfel.ecommerce.model.Response]- is empty=userId == usrid, recommendation == null
#[when][woolfel.ecommerce.model.Response]- is not empty=userId == usrid, recommendation != null
#[when][woolfel.ecommerce.model.Response]- matches the user=userId == userid
#[when][woolfel.ecommerce.model.Aggregate]an aggregate=aggr : Aggregate()
#[when][woolfel.ecommerce.model.Recommendation]the recommendation where=recm : Recommendation()
#[when][woolfel.ecommerce.model.Recommendation]- the profile is found=profileId == prfid
#[when][woolfel.ecommerce.model.Product]the Product=prod : Product()
#[when][woolfel.ecommerce.model.Product]- store is "{store}"=storeCategory == "{store}"
#[when][woolfel.ecommerce.model.Product]- shop category is "{shopcat}"=shopCategory == "{shopcat}"
#[when][woolfel.ecommerce.model.Product]- category is "{prodcat}"=productCategory == "{prodcat}"
#[when][woolfel.ecommerce.model.Product]- subcategory is "{subcat}"=subProductCategory == "{subcat}"
#[when][woolfel.ecommerce.model.Product]- manufacturer is "{manufac}"=manufacturer == "{manufac}"
#[when][woolfel.ecommerce.model.Product]- SKU is equal to "{sku}"=SKU == "{sku}"
#[when][woolfel.ecommerce.model.Order]the Order where=ordr : Order()
#[when][woolfel.ecommerce.model.Order]- shipping method is "{shipmethod}"=shippingMethod == "{shipmethod}"
#[when][woolfel.ecommerce.model.Order]- has more than {items}=cartItems > {items}
#[when][woolfel.ecommerce.model.Order]- has coupons=coupons != null
#[then][woolfel.ecommerce.model.Recommendation]return the recommendation=resp.setRecommendation(recm);
#[then][]Log "{msg}"=System.out.println("{msg}");
#[when][]but not=not
#[when][woolfel.ecommerce.model.Customer]- last name is not "{surname}"=surname != "{surname}"
#[then][*]Show last name=System.out.println(cust.getSurname());
