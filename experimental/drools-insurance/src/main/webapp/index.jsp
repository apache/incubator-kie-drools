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
					<div align="left"><b>Dados do condutor principal</b></div>
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
									<option value=0 <% if (request.getParameter("sexo") == "0") { out.print("selected='selected'"); } %>> Masculino</option>
									<option value=1 <% if (request.getParameter("sexo") == "1") { out.print("selected='selected'"); } %>>Feminino</option>
								</select></td>
							</tr>

							<tr>
								<td class="c1">Data Nascimento:</td>

								<td><input size="2" name="data_nascimento_dia"
									maxlength="2" value="<% out.print(request.getParameter("data_nascimento_dia"));%>" type="text" width="2"> / <input
									size="2" name="data_nascimento_mes" maxlength="2" value="<% out.print(request.getParameter("data_nascimento_mes"));%>"
									type="text"> / <input size="4"
									name="data_nascimento_ano" maxlength="4" value="<% out.print(request.getParameter("data_nascimento_ano"));%>"
									type="text"> <span class="c2">&nbsp; (dd/mm/aaaa
								)</span></td>
							</tr>

							<tr>
								<td class="c1">Tempo de Habilitacao:</td>

								<td><input name="habilitacao" size="2" maxlength="2"
									value="<% out.print(request.getParameter("habilitacao"));%>" type="text"> <span class="c2">Anos</span></td>
							</tr>

							<tr>
								<td class="c1">Estado Civil:</td>

								<td><select name="estadoCivil">
									<option value="0" <% if (request.getParameter("estadoCivil") == "0") { out.print("selected='selected'"); } %> >Solteiro/Divorciado</option>
									<option value="1" <% if (request.getParameter("estadoCivil") == "1") { out.print("selected='selected'"); } %>>Casado/Uniao Estavel Viuvo</option>
								</select></td>
							</tr>

							<tr>
								<td class="c1">Tem filhos?</td>

								<td><select name="temFilhos">
									<option value="1" <% if (request.getParameter("temFilhos") == "1") { out.print("selected='selected'"); } %> >Sim, ate 17 anos.</option>
									<option value="2" <% if (request.getParameter("temFilhos") == "2") { out.print("selected='selected'"); } %> >Sim, entre 17 e 25 anos.</option>
									<option value="3" <% if (request.getParameter("temFilhos") == "3") { out.print("selected='selected'"); } %> >Sim, ate 30 anos.</option>
									<option value="0" <% if (request.getParameter("temFilhos") == "0") { out.print("selected='selected'"); } %> >Nao ou Nenhuma das anteriores</option>
								</select></td>
							</tr>

							<tr>
								<td class="c1">Escolaridade:</td>

								<td><select name="escolaridade">
									<option value="1" selected="selected" >Elementar</option>
									<option value="2">Medio</option>
									<option value="3">Universitario</option>
									<option value="4">Pos Graduacao</option>
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
					<div align="left"><b>Informacoes adicionais do condutor
					principal</b></div>
					</td>
				</tr>

				<tr>
					<td valign="top" bgcolor="#FFFFFF">
					<div align="left"><br>

					<table width="98%" height="188" border="0" align="center"
						cellpadding="4" cellspacing="2">
						<tbody>
							<tr>
								<td width="296" class="c1">Guarda do veiculo de dia:</td>

								<td width="266"><select name="garagemDia">
									<option value="1" <% if (request.getParameter("garagemDia") == "1") { out.print("selected='selected'"); } %>>Garagem fechada</option>
									<option value="2" <% if (request.getParameter("garagemDia") == "2") { out.print("selected='selected'"); } %>>Estacionamento</option>
									<option value="0" <% if (request.getParameter("garagemDia") == "0") { out.print("selected='selected'"); } %> >Nao informado ou na rua</option>
								</select></td>
							</tr>

							<tr>
								<td class="c1">Guarda do veiculo de noite:</td>

								<td><select name="garagemNoite">
									<option value="1" <% if (request.getParameter("garagemNoite") == "1") { out.print("selected='selected'"); } %> >Garagem fechada</option>
									<option value="2" <% if (request.getParameter("garagemNoite") == "2") { out.print("selected='selected'"); } %>>Estacionamento</option>
									<option value="0" <% if (request.getParameter("garagemNoite") == "0") { out.print("selected='selected'"); } %>selected="selected" >Nao informado ou na rua</option>
								</select></td>
							</tr>

							<tr>
								<td class="c1">Residencia atual:</td>

								<td><select name="condicaoImovel">
									<option value="1" <% if (request.getParameter("condicaoImovel") == "1") { out.print("selected='selected'"); } %> >Propria/Familia</option>
									<option value="0" <% if (request.getParameter("condicaoImovel") == "0") { out.print("selected='selected'"); } %>>Alugada</option>
								</select></td>
							</tr>

							<tr>
								<td class="c1">Emprego atual:</td>

								<td><select name="profissao">
									<option value="0" <% if (request.getParameter("profissao") == "0") { out.print("selected='selected'"); } %> >Desempregado</option>
									<option value="1" <% if (request.getParameter("profissao") == "1") { out.print("selected='selected'"); } %>>Proprietario de Empresa ou Negocio</option>
									<option value="2" <% if (request.getParameter("profissao") == "2") { out.print("selected='selected'"); } %>>Funcionario Publico</option>
									<option value="3" <% if (request.getParameter("profissao") == "3") { out.print("selected='selected'"); } %>>Funcionario Privado</option>
									<option value="4" <% if (request.getParameter("profissao") == "4") { out.print("selected='selected'"); } %>>Estudante</option>
								</select></td>
							</tr>

							<tr>
								<td class="c1">Reside em:</td>

								<td><select name="tipoResidencia">
									<option value="0"  <% if (request.getParameter("tipoResidencia") == "0") { out.print("selected='selected'"); } %> >Casa</option>
									<option value="1"  <% if (request.getParameter("tipoResidencia") == "1") { out.print("selected='selected'"); } %>>Casa em condominio fechado</option>
									<option value="2"  <% if (request.getParameter("tipoResidencia") == "2") { out.print("selected='selected'"); } %>>Apartamento</option>
								</select></td>
							</tr>

							<tr>
								<td class="c1">Quantos sinistros indenizados ?</td>

								<td><select name="sinistros">
									<option value="0" <% if (request.getParameter("sinistros") == "0") { out.print("selected='selected'"); } %>>Nenhum</option>
									<option value="1" <% if (request.getParameter("sinistros") == "1") { out.print("selected='selected'"); } %>>1</option>
									<option value="2" <% if (request.getParameter("sinistros") == "2") { out.print("selected='selected'"); } %>>2</option>
									<option value="3" <% if (request.getParameter("sinistros") == "3") { out.print("selected='selected'"); } %>>3</option>
									<option value="4"<% if (request.getParameter("sinistros") == "4") { out.print("selected='selected'"); } %>>4</option>
									<option value="5"<% if (request.getParameter("sinistros") == "5") { out.print("selected='selected'"); } %>>5</option>
									<option value="6"<% if (request.getParameter("sinistros") == "6") { out.print("selected='selected'"); } %>>6</option>
									<option value="7"<% if (request.getParameter("sinistros") == "7") { out.print("selected='selected'"); } %>>7</option>
									<option value="8"<% if (request.getParameter("sinistros") == "8") { out.print("selected='selected'"); } %>>8</option>
									<option value="9"<% if (request.getParameter("sinistros") == "9") { out.print("selected='selected'"); } %>>mais de 8</option>
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
					<div align="left"><span class="seucorretor2 seucorretor2"><b>Clausulas
					complementares</b></span></div>
					</td>
				</tr>

				<tr>
					<td valign="top" bordercolor="#000000" bgcolor="#FFFFFF">
					<div align="left"><br>

					<table align="center" border="0" cellpadding="2" cellspacing="2"
						width="95%">
						<tbody>
							<tr>
								<td class="c1" width="50%">Quer Carro Reserva?</td>

								<td width="50%"><select name="carroReserva">
									<option value="1" <% if (request.getParameter("carroReserva") == "1") { out.print("selected='selected'"); } %>>Sim</option>
									<option value="0" <% if (request.getParameter("carroReserva") == "0") { out.print("selected='selected'"); } %>>Nao</option>
								</select> &nbsp;</td>
							</tr>

							<tr>
								<td class="c1" width="50%">Quer Assistencia 24 horas?</td>
								<td width="50%"><select name="assistencia24h">
									<option value="1" <% if (request.getParameter("assistencia24h") == "1") { out.print("selected='selected'"); } %>>Sim</option>
									<option value="0" <% if (request.getParameter("assistencia24h") == "0") { out.print("selected='selected'"); } %>>Nao</option>									
								</select> &nbsp;</td>
							</tr>

							<tr>
								<td class="c1" width="50%">Quer Cobertura de vidros?</td>

								<td width="50%"><select name="vidros">
									<option value="1" <% if (request.getParameter("vidros") == "1") { out.print("selected='selected'"); } %>>Sim</option>
									<option value="0" <% if (request.getParameter("vidros") == "0") { out.print("selected='selected'"); } %> >Nao</option>									
								</select> &nbsp;</td>
							</tr>

							<tr>
								<td class="c1" width="50%">Despesas Extraordinarias:</td>

								<td width="50%"><select name="despExtra">
									<option value="1" <% if (request.getParameter("despExtra") == "1") { out.print("selected='selected'"); } %>>Sim</option>
									<option value="0" <% if (request.getParameter("despExtra") == "0") { out.print("selected='selected'"); } %>>Nao</option>
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
					<div align="left"><span class="seucorretor1"><b>Cobertura
					de Acess—rios:</b></span></div>
					</td>
				</tr>

				<tr>
					<td valign="top" bordercolor="#000000" bgcolor="#FFFFFF">
					<div align="left"><br>

					<table align="center" border="0" cellpadding="2" cellspacing="2"
						width="95%">
						<tbody>
							<tr>
								<td class="c5" width="50%">Descricao</td>

								<td class="c5" width="50%">Valor (R$)</td>
							</tr>

							<tr>
								<td class="c1" width="50%">Alarme:</td>

								<td width="50%"><input size="12" name="alarme" value="<% out.print(request.getParameter("alarme"));%>"
									type="text"> &nbsp;</td>
							</tr>

							<tr>
								<td class="c1" width="50%">Sistema de Som:</td>

								<td width="50%"><input size="12" name="sistemaSom"
									value="<% out.print(request.getParameter("sistemaSom"));%>" type="text"> &nbsp;</td>
							</tr>

							<tr>
								<td class="c1" width="50%">Blindagem:</td>

								<td width="50%"><input size="12" name="blindagem"
									value="<% out.print(request.getParameter("blindagem"));%>" type="text"> &nbsp;</td>
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
