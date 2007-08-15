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
									<option value=0 selected="selected"> Masculino</option>
									<option value=1>Feminino</option>
								</select></td>
							</tr>

							<tr>
								<td class="c1">Data Nascimento:</td>

								<td><input size="2" name="data_nascimento_dia"
									maxlength="2" value="01" type="text" width="2"> / <input
									size="2" name="data_nascimento_mes" maxlength="2" value="01"
									type="text"> / <input size="4"
									name="data_nascimento_ano" maxlength="4" value="1900"
									type="text"> <span class="c2">&nbsp; (dd/mm/aaaa
								)</span></td>
							</tr>

							<tr>
								<td class="c1">Tempo de Habilitacao:</td>

								<td><input name="habilitacao" size="2" maxlength="2"
									value="0" type="text"> <span class="c2">Anos</span></td>
							</tr>

							<tr>
								<td class="c1">Estado Civil:</td>

								<td><select name="estadoCivil">
									<option value="0" selected="selected" >Solteiro/Divorciado</option>
									<option value="1">Casado/Uniao Estavel Viuvo</option>
								</select></td>
							</tr>

							<tr>
								<td class="c1">Tem filhos?</td>

								<td><select name="temFilhos">
									<option value="1">Sim, até 17 anos.</option>
									<option value="2">Sim, entre 17 e 25 anos.</option>
									<option value="3">Sim, até 30 anos.</option>
									<option value="0" selected="selected" >Não ou Nenhuma das anteriores</option>
								</select></td>
							</tr>

							<tr>
								<td class="c1">Escolaridade:</td>

								<td><select name="escolaridade">
									<option value="1" selected="selected" >1 Grau</option>
									<option value="2">2 Grau</option>
									<option value="3">3 Grau (Univ.)</option>
									<option value="4">Pós Graduação</option>
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
									<option value="1">Garagem fechada</option>
									<option value="2">Estacionamento</option>
									<option value="0" selected="selected" >Não informado ou na rua</option>
								</select></td>
							</tr>

							<tr>
								<td class="c1">Guarda do veículo de noite:</td>

								<td><select name="garagemNoite">
									<option value="1">Garagem fechada</option>
									<option value="2">Estacionamento</option>
									<option value="0" selected="selected" >Não informado ou na rua</option>
								</select></td>
							</tr>

							<tr>
								<td class="c1">Residência atual:</td>

								<td><select name="condicaoImovel">
									<option value="1">Própria/Familia</option>
									<option value="0" selected="selected">Alugada</option>
								</select></td>
							</tr>

							<tr>
								<td class="c1">Emprego atual:</td>

								<td><select name="profissao">
									<option value="0" selected="selected" >Desempregado</option>
									<option value="1">Proprietário de Empresa ou Negócio</option>
									<option value="2">Funcionario Publico</option>
									<option value="3">Funcionario Privado</option>
									<option value="4">Estudante</option>
								</select></td>
							</tr>

							<tr>
								<td class="c1">Reside em:</td>

								<td><select name="tipoResidencia">
									<option value="0" selected="selected" >Casa</option>
									<option value="1">Casa em condomínio fechado</option>
									<option value="2">Apartamento</option>
								</select></td>
							</tr>

							<tr>
								<td class="c1">Quantos sinistros indenizados ?</td>

								<td><select name="sinistros">
									<option value="0" selected="selected">Nenhum</option>
									<option value="1">1</option>
									<option value="2">2</option>
									<option value="3">3</option>
									<option value="4">4</option>
									<option value="5">5</option>
									<option value="6">6</option>
									<option value="7">7</option>
									<option value="8">8</option>
									<option value="9">mais de 8</option>
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
									<option value="1">Sim</option>
									<option value="0" selected="selected" >Não</option>
								</select> &nbsp;</td>
							</tr>

							<tr>
								<td class="c1" width="50%">Quer Assistência 24 horas?</td>
								<td width="50%"><select name="assistencia24h">
									<option value="1">Sim</option>
									<option value="0" selected="selected" >Não</option>									
								</select> &nbsp;</td>
							</tr>

							<tr>
								<td class="c1" width="50%">Quer Cobertura de vidros?</td>

								<td width="50%"><select name="vidros">
									<option value="1">Sim</option>
									<option value="0" selected="selected" >Não</option>									
								</select> &nbsp;</td>
							</tr>

							<tr>
								<td class="c1" width="50%">Despesas Extraordinárias:</td>

								<td width="50%"><select name="despExtra">
									<option value="1">Sim</option>
									<option value="0" selected="selected" >Não</option>
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
					de Acessórios:</b></span></div>
					</td>
				</tr>

				<tr>
					<td valign="top" bordercolor="#000000" bgcolor="#FFFFFF">
					<div align="left"><br>

					<table align="center" border="0" cellpadding="2" cellspacing="2"
						width="95%">
						<tbody>
							<tr>
								<td class="c5" width="50%">Descrição</td>

								<td class="c5" width="50%">Valor (R$)</td>
							</tr>

							<tr>
								<td class="c1" width="50%">Alarme:</td>

								<td width="50%"><input size="12" name="alarme" value="0.00"
									type="text"> &nbsp;</td>
							</tr>

							<tr>
								<td class="c1" width="50%">Sistema de Som:</td>

								<td width="50%"><input size="12" name="sistemaSom"
									value="0.00" type="text"> &nbsp;</td>
							</tr>

							<tr>
								<td class="c1" width="50%">Blindagem:</td>

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
