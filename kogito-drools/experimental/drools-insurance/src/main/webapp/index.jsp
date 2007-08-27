<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN">

<html>
<head>
<title>Calculo de seguro</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link href="drools.css" media="screen" rel="Stylesheet" type="text/css">
</head>

<body>
<div id="header">
<div class="area">
<div class="column">
<ul class="options">
	<li class="new"><a href="#"><span>Drools Insurance
	Company</span></a></li>
</ul>
</div>
</div>
</div>

<form action="InsuranceServlet" method="get" accept-charset="utf-8">

<table align="center">
	<tr>
		<td>
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
								<td width="162" class="c1">Sexo:</td>

								<td width="363"><select name="sexo">
									<option value=0 selected="selected">Male</option>
									<option value=1>Female</option>
								</select></td>
							</tr>

							<tr>
								<td class="c1">Birth Date:</td>

								<td><input size="2" name="data_nascimento_dia"
									maxlength="2" value="01" type="text" width="2"> / <input
									size="2" name="data_nascimento_mes" maxlength="2" value="01"
									type="text"> / <input size="4"
									name="data_nascimento_ano" maxlength="4" value="1900"
									type="text"> <span class="c2">&nbsp; (dd/mm/aaaa
								)</span></td>
							</tr>

							<tr>
								<td class="c1">License Age:</td>

								<td><input name="habilitacao" size="2" maxlength="2"
									value="0" type="text"> <span class="c2">Years</span></td>
							</tr>

							<tr>
								<td class="c1">Marital State:</td>

								<td><select name="estadoCivil">
									<option value="0" selected="selected" >Single/Divorced</option>
									<option value="1">Maried</option>
								</select></td>
							</tr>

							<tr>
								<td class="c1">Has Child?</td>

								<td><select name="temFilhos">
									<option value="1">Yes, less than 17 years.</option>
									<option value="2">Yes, Between 17 and 25 years.</option>
									<option value="3">Yes, greater than 26 years old.</option>
									<option value="0" selected="selected" >No, I don't have Child</option>
								</select></td>
							</tr>

							<tr>
								<td class="c1">Degree:</td>

								<td><select name="escolaridade">
									<option value="1" selected="selected" >Elementary School</option>
									<option value="2">High School</option>
									<option value="3">College</option>
									<option value="4">Pos</option>
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
		</td>
		<td>
		<table width="600" border="0" align="center" cellpadding="0"
			cellspacing="2" bgcolor="#999999" class="seucorretor6">
			<tbody>
				<tr class="seucorretor6">
					<td class="seucorretor2" align="center">
					<div align="left"><b>Driver's additional info </b></div>
					</td>
				</tr>

				<tr>
					<td valign="top" bgcolor="#FFFFFF">
					<div align="left"><br>

					<table width="98%" height="188" border="0" align="center"
						cellpadding="4" cellspacing="2">
						<tbody>
							<tr>
								<td width="296" class="c1">Day veicle place:</td>

								<td width="266"><select name="garagemDia">
									<option value="1">Closed Garage</option>
									<option value="2">Parking</option>
									<option value="0" selected="selected" >Street</option>
								</select></td>
							</tr>

							<tr>
								<td class="c1">Night veichle place:</td>

								<td><select name="garagemNoite">
                                    <option value="1">Closed Garage</option>
                                    <option value="2">Parking</option>
                                    <option value="0" selected="selected" >Street</option>
								</select></td>
							</tr>

							<tr>
								<td class="c1">Residence Status:</td>

								<td><select name="condicaoImovel">
									<option value="1">Owned</option>
									<option value="0" selected="selected">Rented</option>
								</select></td>
							</tr>

							<tr>
								<td class="c1">Actual Job:</td>

								<td><select name="profissao">
									<option value="0" selected="selected" >Desempregado</option>
									<option value="1">Business owner</option>
									<option value="2">Public employee</option>
									<option value="3">Private employee</option>
									<option value="4">Student</option>
								</select></td>
							</tr>

							<tr>
								<td class="c1">Lives</td>

								<td><select name="tipoResidencia">
									<option value="0" selected="selected" >House</option>
									<option value="1">Private village </option>
									<option value="2">Appartment</option>
								</select></td>
							</tr>

							<tr>
								<td class="c1">previous claim numbers</td>

								<td><select name="sinistros">
									<option value="0" selected="selected">none</option>
									<option value="1">1</option>
									<option value="2">2</option>
									<option value="3">3</option>
									<option value="4">4</option>
									<option value="5">5</option>
									<option value="6">6</option>
									<option value="7">7</option>
									<option value="8">8</option>
									<option value="9">more than 8</option>
								</select></td>
							</tr>
						</tbody>
					</table>
					</div>
					</td>
				</tr>
			</tbody>
		</table>

		</td>
	</tr>

	<tr>
		<td>
		<table width="600" border="0" align="center" cellpadding="0"
			cellspacing="2" bgcolor="#999999" class="seucorretor6">
			<tbody>
				<tr class="seucorretor6">
					<td align="center" bordercolor="#000000" class="seucorretor2">
					<div align="left"><span class="seucorretor2 seucorretor2"><b>Supplemental thing</b></span></div>
					</td>
				</tr>

				<tr>
					<td valign="top" bordercolor="#000000" bgcolor="#FFFFFF">
					<div align="left"><br>

					<table align="center" border="0" cellpadding="2" cellspacing="2"
						width="95%">
						<tbody>
							<tr>
								<td class="c1" width="50%">Whants extra car?</td>

								<td width="50%"><select name="carroReserva">
									<option value="1">Yes</option>
									<option value="0" selected="selected" >No</option>
								</select> &nbsp;</td>
							</tr>

							<tr>
								<td class="c1" width="50%">Wants 24/7 support?</td>
								<td width="50%"><select name="assistencia24h">
                                    <option value="1">Yes</option>
                                    <option value="0" selected="selected" >No</option>
								</select> &nbsp;</td>
							</tr>

							<tr>
								<td class="c1" width="50%">Glass coverage?</td>

								<td width="50%"><select name="vidros">
                                    <option value="1">Yes</option>
                                    <option value="0" selected="selected" >No</option>
								</select> &nbsp;</td>
							</tr>

							<tr>
								<td class="c1" width="50%">Unrelated expenses:</td>

								<td width="50%"><select name="despExtra">
                                    <option value="1">Yes</option>
                                    <option value="0" selected="selected" >No</option>
								</select> &nbsp;</td>
							</tr>
						</tbody>
					</table>
					</div>
					</td>
				</tr>
			</tbody>
		</table>
		</td>

		<td>
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

								<td width="50%"><input size="12" name="alarme" value="0.00"
									type="text"> &nbsp;</td>
							</tr>

							<tr>
								<td class="c1" width="50%">Sound system:</td>

								<td width="50%"><input size="12" name="sistemaSom"
									value="0.00" type="text"> &nbsp;</td>
							</tr>

							<tr>
								<td class="c1" width="50%">Armor:</td>

								<td width="50%"><input size="12" name="blindagem"
									value="0.00" type="text"> &nbsp;</td>
							</tr>
						</tbody>
					</table>
					</div>
					</td>
				</tr>
			</tbody>
		</table>
		</td>
	</tr>

</table>




<p>&nbsp;</p>
<p><input name="Reset" type="reset" value="Reset"> <input
	type="submit" value="Continue"></p>
</form>


<%
	try {
		if ( request.getParameter("calculo") != null ) {
%>
	tabe
	<table width="600" border="0" align="center" cellpadding="0"
			cellspacing="2" bgcolor="#999999" class="seucorretor6">
      <tbody>
        <tr class="seucorretor6">
          <td align="center" bordercolor="#000000" class="seucorretor2"><div align="left"><span class="seucorretor2 seucorretor2"><b>Calculo de Seguro</b></span></div></td>
        </tr>
        <tr>
          <td valign="top" bordercolor="#000000" bgcolor="#FFFFFF"><div align="left"><br>
                  <table align="center" border="0" cellpadding="2" cellspacing="2"
						width="95%">
                    <tbody>
                      <tr>
                        <td class="c1" width="50%">Seguro aprovado</td>
                        <td width="50%"><%  out.print(request.getParameter("aprovado")); %></td>
                      </tr>
                      <tr>
                        <td class="c1" width="50%">Fator risco</td>
                        <td width="50%"><% out.print(request.getParameter("fatorrisco"));%></td>
                      </tr>
                      <tr>
                        <td class="c1" width="50%">Preco Base Apolice</td>
                        <td width="50%"><% out.print(request.getParameter("base")); %></td>
                      </tr>
                      <tr>
                        <td class="c1" width="50%">Valor do Seguro</td>
                        <td width="50%"><% out.print(request.getParameter("valor")); %></td>
                      </tr>
                    </tbody>
                  </table>
          </div></td>
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
