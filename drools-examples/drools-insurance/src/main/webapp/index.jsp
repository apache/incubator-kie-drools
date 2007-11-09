<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN">

<html>
<head>
    <title>Drools Insurance Company</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <link href="drools.css" media="screen" rel="Stylesheet" type="text/css">

	<script language = "Javascript">	
	function validateNumber(number){
		var i;
		var s = number.value;
		
	    for (i = 0; i < s.length; i++){   
	        var c = s.charAt(i);
	        if (((c < "0") || (c > "9"))) {
			    alert("The "+ number.name +" field should be numeric");
				return false;
			}
	    }
	    return true;
	}
	

	function validateMoney(money)
	{
		var format = /^\d+\.\d{2}$/; 
	    if(money.value.length !=0 && format.test(money.value))
	    {
			return true;
	    }
        alert( money.name +  " should be a valid currency value");
        return false;
	}
	
	function validateDate(fld) {
	    var RegExPattern = /^(?=\d)(?:(?:(?:(?:(?:0?[13578]|1[02])(\/|-|\.)31)\1|(?:(?:0?[1,3-9]|1[0-2])(\/|-|\.)(?:29|30)\2))(?:(?:1[6-9]|[2-9]\d)?\d{2})|(?:0?2(\/|-|\.)29\3(?:(?:(?:1[6-9]|[2-9]\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))|(?:(?:0?[1-9])|(?:1[0-2]))(\/|-|\.)(?:0?[1-9]|1\d|2[0-8])\4(?:(?:1[6-9]|[2-9]\d)?\d{2}))($|\ (?=\d)))?(((0?[1-9]|1[012])(:[0-5]\d){0,2}(\ [AP]M))|([01]\d|2[0-3])(:[0-5]\d){1,2})?$/;
	    var errorMessage = 'Please enter valid date as month, day, and four digit year.\nYou may use a slash, hyphen or period to separate the values.\nThe date must be a real date. 2-30-2000 would not be accepted.\nFormay mm/dd/yyyy.';
	    if ((fld.value.match(RegExPattern)) && (fld.value!='')) {
	        return true;
	    } else {
	        alert(errorMessage);
			return false;
	    } 
	}
	
	function ValidateForm(){
		var dt = document.frmSample.txtDate
		if (validateDate(dt) == false){
			dt.focus()
			return false
		}
	    return true
	 }

	</script>

</head>

<body>
<div id="header">
    <div class="area">
        <div class="column">
            <ul class="options">
                <li class="new"><a href="#"><span>Drools Insurancea
	Company</span></a></li>
            </ul>
        </div>
    </div>
</div>

<form action="InsuranceServlet" method="get" accept-charset="utf-8" >

    <table width="600" height="236" border="0" align="center"
           cellpadding="0" cellspacing="2" bgcolor="#999999"
           class="seucorretor6">
        <tbody>
            <tr class="seucorretor6">
                <td class="seucorretor2" align="center">
                    <div align="left"><b>Driver's information</b></div>
                </td>
            </tr>

            <tr>
                <td align="center" bgcolor="#FFFFFF" valign="top">
                    <div align="left"><br>

                        <table border="0" cellpadding="3" cellspacing="0" width="584">
                            <tbody>
                                <tr>
                                    <td width="162" class="c1">Genre:</td>

                                    <td width="363"><select name="sexo">
                                        <option value=0 <% if (request.getParameter("sexo") == "0") {
                                            out.print("selected='selected'");
                                        } %>> Male
                                        </option>
                                        <option value=1 <% if (request.getParameter("sexo") == "1") {
                                            out.print("selected='selected'");
                                        } %>>Female
                                        </option>
                                    </select></td>
                                </tr>

                                <tr>
                                    <td class="c1">Birth Date:</td>

									 <td>

									<input type="text" name="birthdate" onblur="validateDate(this);" value="<% 
											if ( request.getParameter("birthdate") == null ) { 
												out.print("09/18/1983");
											} else { 
												out.print(request.getParameter("birthdate"));
											}
										%>">> (mm/dd/yyyy)
									</td>
                                </tr>

                                <tr>
                                    <td class="c1">License Age:</td>

                                    <td><input name="habilitacao" size="2" maxlength="2" type="text" onblur="validateNumber(this);"
                                               value="<% 
											if ( request.getParameter("habilitacao") == null ) { 
												out.print("0");
											} else { 
												out.print(request.getParameter("habilitacao")); 
											} %>">
                                        <span class="c2">Years</span></td>
                                </tr>

                                <tr>
                                    <td class="c1">Marital State:</td>

                                    <td><select name="estadoCivil">
                                        <option value="0" <% if (request.getParameter("estadoCivil") == "0") {
                                            out.print("selected='selected'");
                                        } %> > Single
                                        </option>
                                        <option value="1" <% if (request.getParameter("estadoCivil") == "1") {
                                            out.print("selected='selected'");
                                        } %>>Married
                                        </option>
                                    </select></td>
                                </tr>

                                <tr>
                                    <td class="c1">Has Child?</td>

                                    <td><select name="temFilhos">
                                        <option value="1" <% if (request.getParameter("temFilhos") == "1") {
                                            out.print("selected='selected'");
                                        } %> >Yes, less than 17 years.
                                        </option>
                                        <option value="2" <% if (request.getParameter("temFilhos") == "2") {
                                            out.print("selected='selected'");
                                        } %> >Yes, Between 17 and 25 years.
                                        </option>
                                        <option value="3" <% if (request.getParameter("temFilhos") == "3") {
                                            out.print("selected='selected'");
                                        } %> >Yes, greater than 26 years old.
                                        </option>
                                        <option value="0" <% if (request.getParameter("temFilhos") == "0") {
                                            out.print("selected='selected'");
                                        } %> >No, I don't have Child
                                        </option>
                                    </select></td>
                                </tr>

                                <tr>
                                    <td class="c1">Degree:</td>

                                    <td><select name="escolaridade">
                                        <option value="1" selected="selected">Elementary School</option>
                                        <option value="2">High School</option>
                                        <option value="3">College</option>
                                        <option value="4">MSC or high</option>
                                    </select></td>
                                </tr>
                            </tbody>
                        </table>
                        <br>
                    </div>
                </td>
            </tr>
        </tbody>
    </table>
<br/><br/>
    <table width="600" border="0" align="center" cellpadding="0"
           cellspacing="2" bgcolor="#999999" class="seucorretor6">
        <tbody>
            <tr class="seucorretor6">
                <td class="seucorretor2" align="center">
                    <div align="left"><b>Driver's additional info</b></div>
                </td>
            </tr>

            <tr>
                <td valign="top" bgcolor="#FFFFFF">
                    <div align="left"><br>

                        <table width="98%" height="188" border="0" align="center"
                               cellpadding="4" cellspacing="2">
                            <tbody>
                                <tr>
                                    <td width="296" class="c1">Day vehicle place:</td>

                                    <td width="266"><select name="garagemDia">
                                        <option value="1" <% if (request.getParameter("garagemDia") == "1") {
                                            out.print("selected='selected'");
                                        } %>>Garage
                                        </option>
                                        <option value="2" <% if (request.getParameter("garagemDia") == "2") {
                                            out.print("selected='selected'");
                                        } %>>Parking
                                        </option>
                                        <option value="0" <% if (request.getParameter("garagemDia") == "0") {
                                            out.print("selected='selected'");
                                        } %> >Street
                                        </option>
                                    </select></td>
                                </tr>

                                <tr>
                                    <td class="c1">Night vehicle place:</td>

                                    <td><select name="garagemNoite">
                                        <option value="1" <% if (request.getParameter("garagemNoite") == "1") {
                                            out.print("selected='selected'");
                                        } %> >Garage
                                        </option>
                                        <option value="2" <% if (request.getParameter("garagemNoite") == "2") {
                                            out.print("selected='selected'");
                                        } %>>Parking
                                        </option>
                                        <option value="0"
                                                <% if (request.getParameter("garagemNoite") == "0") { out.print("selected='selected'"); } %>selected="selected">
                                            Street
                                        </option>
                                    </select></td>
                                </tr>

                                <tr>
                                    <td class="c1">Residence Status:</td>

                                    <td><select name="condicaoImovel">
                                        <option value="1" <% if (request.getParameter("condicaoImovel") == "1") {
                                            out.print("selected='selected'");
                                        } %> >Owned
                                        </option>
                                        <option value="0" <% if (request.getParameter("condicaoImovel") == "0") {
                                            out.print("selected='selected'");
                                        } %>>Rent
                                        </option>
                                    </select></td>
                                </tr>

                                <tr>
                                    <td class="c1">Actual Job:</td>

                                    <td><select name="profissao">
                                        <option value="0" <% if (request.getParameter("profissao") == "0") {
                                            out.print("selected='selected'");
                                        } %> >Unemployed
                                        </option>
                                        <option value="1" <% if (request.getParameter("profissao") == "1") {
                                            out.print("selected='selected'");
                                        } %>>Business owner
                                        </option>
                                        <option value="2" <% if (request.getParameter("profissao") == "2") {
                                            out.print("selected='selected'");
                                        } %>>Public employee
                                        </option>
                                        <option value="3" <% if (request.getParameter("profissao") == "3") {
                                            out.print("selected='selected'");
                                        } %>>Private employee
                                        </option>
                                        <option value="4" <% if (request.getParameter("profissao") == "4") {
                                            out.print("selected='selected'");
                                        } %>>Student
                                        </option>
                                    </select></td>
                                </tr>

                                <tr>
                                    <td class="c1">Residence:</td>

                                    <td><select name="tipoResidencia">
                                        <option value="0"  <% if (request.getParameter("tipoResidencia") == "0") {
                                            out.print("selected='selected'");
                                        } %> >House
                                        </option>
                                        <option value="1"  <% if (request.getParameter("tipoResidencia") == "1") {
                                            out.print("selected='selected'");
                                        } %>>Private village
                                        </option>
                                        <option value="2"  <% if (request.getParameter("tipoResidencia") == "2") {
                                            out.print("selected='selected'");
                                        } %>>Apartament
                                        </option>
                                    </select></td>
                                </tr>

                                <tr>
                                    <td class="c1">Previous claim numbers?</td>

                                    <td><select name="sinistros">
                                        <option value="0" <% if (request.getParameter("sinistros") == "0") {
                                            out.print("selected='selected'");
                                        } %>>None
                                        </option>
                                        <option value="1" <% if (request.getParameter("sinistros") == "1") {
                                            out.print("selected='selected'");
                                        } %>>1
                                        </option>
                                        <option value="2" <% if (request.getParameter("sinistros") == "2") {
                                            out.print("selected='selected'");
                                        } %>>2
                                        </option>
                                        <option value="3" <% if (request.getParameter("sinistros") == "3") {
                                            out.print("selected='selected'");
                                        } %>>3
                                        </option>
                                        <option value="4"<% if (request.getParameter("sinistros") == "4") {
                                            out.print("selected='selected'");
                                        } %>>4
                                        </option>
                                        <option value="5"<% if (request.getParameter("sinistros") == "5") {
                                            out.print("selected='selected'");
                                        } %>>5
                                        </option>
                                        <option value="6"<% if (request.getParameter("sinistros") == "6") {
                                            out.print("selected='selected'");
                                        } %>>6
                                        </option>
                                        <option value="7"<% if (request.getParameter("sinistros") == "7") {
                                            out.print("selected='selected'");
                                        } %>>7
                                        </option>
                                        <option value="8"<% if (request.getParameter("sinistros") == "8") {
                                            out.print("selected='selected'");
                                        } %>>8
                                        </option>
                                        <option value="9"<% if (request.getParameter("sinistros") == "9") {
                                            out.print("selected='selected'");
                                        } %>>more than 8
                                        </option>
                                    </select></td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </td>
            </tr>
        </tbody>
    </table>
<br/><br/>
    <table width="600" border="0" align="center" cellpadding="0"
           cellspacing="2" bgcolor="#999999" class="seucorretor6">
        <tbody>
            <tr class="seucorretor6">
                <td align="center" bordercolor="#000000" class="seucorretor2">
                    <div align="left"><span class="seucorretor2 seucorretor2"><b>Supplemental 
                        coverage</b></span></div>
                </td>
            </tr>

            <tr>
                <td valign="top" bordercolor="#000000" bgcolor="#FFFFFF">
                    <div align="left"><br>

                        <table align="center" border="0" cellpadding="2" cellspacing="2"
                               width="95%">
                            <tbody>
                                <tr>
                                    <td class="c1" width="50%">Extra car?</td>

                                    <td width="50%"><select name="carroReserva">
                                        <option value="1" <% if (request.getParameter("carroReserva") == "1") {
                                            out.print("selected='selected'");
                                        } %>>Yes
                                        </option>
                                        <option value="0" <% if (request.getParameter("carroReserva") == "0") {
                                            out.print("selected='selected'");
                                        } %>>No
                                        </option>
                                    </select> &nbsp;</td>
                                </tr>

                                <tr>
                                    <td class="c1" width="50%">24 hours assistance?</td>
                                    <td width="50%"><select name="assistencia24h">
                                        <option value="1" <% if (request.getParameter("assistencia24h") == "1") {
                                            out.print("selected='selected'");
                                        } %>>Yes
                                        </option>
                                        <option value="0" <% if (request.getParameter("assistencia24h") == "0") {
                                            out.print("selected='selected'");
                                        } %>>No
                                        </option>
                                    </select> &nbsp;</td>
                                </tr>

                                <tr>
                                    <td class="c1" width="50%">Glasses coverage?</td>

                                    <td width="50%"><select name="vidros">
                                        <option value="1" <% if (request.getParameter("vidros") == "1") {
                                            out.print("selected='selected'");
                                        } %>>Yes
                                        </option>
                                        <option value="0" <% if (request.getParameter("vidros") == "0") {
                                            out.print("selected='selected'");
                                        } %> >No
                                        </option>
                                    </select> &nbsp;</td>
                                </tr>

                                <tr>
                                    <td class="c1" width="50%">Unrelated expenses:</td>

                                    <td width="50%"><select name="despExtra">
                                        <option value="1" <% if (request.getParameter("despExtra") == "1") {
                                            out.print("selected='selected'");
                                        } %>>Yes
                                        </option>
                                        <option value="0" <% if (request.getParameter("despExtra") == "0") {
                                            out.print("selected='selected'");
                                        } %>>No
                                        </option>
                                    </select> &nbsp;</td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </td>
            </tr>
        </tbody>
    </table>
<br/><br/>
    <table width="600" border="0" align="center" cellpadding="0"
           cellspacing="2" bgcolor="#999999" class="seucorretor6">
        <tbody>
            <tr class="seucorretor6">
                <td align="center" bordercolor="#000000" class="seucorretor2">
                    <div align="left"><span class="seucorretor1"><b>Accessories Coverage:</b></span></div>
                </td>
            </tr>

            <tr>
                <td valign="top" bordercolor="#000000" bgcolor="#FFFFFF">
                    <div align="left"><br>

                        <table align="center" border="0" cellpadding="2" cellspacing="2"
                               width="95%">
                            <tbody>
                                <tr>
                                    <td class="c5" width="50%"></td>

                                    <td class="c5" width="50%">Value ($)</td>
                                </tr>

                                <tr>
                                    <td class="c1" width="50%">Alarm:</td>

                                    <td width="50%"><input size="12" name="alarme" onblur="validateMoney(this);" 
                                                           value="<% 
														if ( request.getParameter("alarme") == null ) { 
															out.print("0.00");
														} else { 
															out.print(request.getParameter("alarme"));
														}
														%>"
                                                           type="text"> &nbsp;</td>
                                </tr>

                                <tr>
                                    <td class="c1" width="50%">Sound:</td>

                                    <td width="50%"><input size="12" name="sistemaSom" onblur="validateMoney(this);"
                                                           value="<% 
														if ( request.getParameter("sistemaSom") == null ) { 
															out.print("0.00");
														} else { 
															out.print(request.getParameter("sistemaSom"));
														}
														%>" type="text"> &nbsp;</td>
                                </tr>

                                <tr>
                                    <td class="c1" width="50%">Armor:</td>
                                    <td width="50%"><input size="12" name="blindagem" onblur="validateMoney(this);"
                                                           value="<%	
															if ( request.getParameter("blindagem") == null ) { 
																out.print("0.00");
															} else { 
																out.print(request.getParameter("blindagem"));
															}
															%>"
                                                           type="text"> &nbsp;</td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </td>
            </tr>
        </tbody>
    </table>



<p>&nbsp;</p>

<p><input name="Reset" type="reset" value="Reset"> <input
        type="submit" value="Continue"></p>
</form>


<%
    try {
        if (request.getParameter("calculo") != null) {
%>
tabe
<table width="600" border="0" align="center" cellpadding="0"
       cellspacing="2" bgcolor="#999999" class="seucorretor6">
    <tbody>
        <tr class="seucorretor6">
            <td align="center" bordercolor="#000000" class="seucorretor2">
                <div align="left"><span class="seucorretor2 seucorretor2"><b>Insurance Calcule</b></span></div>
            </td>
        </tr>
        <tr>
            <td valign="top" bordercolor="#000000" bgcolor="#FFFFFF">
                <div align="left"><br>
                    <table align="center" border="0" cellpadding="2" cellspacing="2"
                           width="95%">
                        <tbody>
                            <tr>
                                <td class="c1" width="50%">is Approved</td>
                                <td width="50%"><% out.print(request.getParameter("aprovado")); %></td>
                            </tr>
                            <tr>
                                <td class="c1" width="50%">Insurance risk Factor</td>
                                <td width="50%"><% out.print(request.getParameter("fatorrisco"));%></td>
                            </tr>
                            <tr>
                                <td class="c1" width="50%">Base insurance Price</td>
                                <td width="50%"><% out.print(request.getParameter("base")); %></td>
                            </tr>
                            <tr>
                                <td class="c1" width="50%">Final Insurance price</td>
                                <td width="50%"><b><% out.print(request.getParameter("valor")); %></b></td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </td>
        </tr>
    </tbody>
</table>
<%
    }
} catch (Exception e) {
%>

<%
    }
%>


<p>&nbsp;</p>

<p><br>
</p>
</body>
</html>
