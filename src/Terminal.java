import java.util.List;
import static javax.swing.JOptionPane.showInputDialog;
import static javax.swing.JOptionPane.showMessageDialog;

public class Terminal {

    private Banco banco;

    public void run() {
        try {
            this.banco = new Banco("Mogi International Bank");
            Cliente atualCliente = null;
            Conta atualConta = null;
            while (true) {

                try {
                    String prompt = "";
                        prompt += "\n  1. criar cliente";
                        prompt += "\n  2. listar clientes";
                        prompt += "\n  3. selectionar cliente";
                        prompt += "\n  4. criar conta";
                        prompt += "\n  5. listar contas";
                        prompt += "\n  6. selecionar conta";
                        prompt += "\n  7. depositar";
                        prompt += "\n  8. sacar";
                        prompt += "\n  9. lista todas as contas";
                        prompt += "\n  10. remover conta do cliente";
                        prompt += "\n  11. remover cliente do banco";
                        prompt += "\n  r. render ";
                        prompt += "\n  exit para sair ";
                    prompt +=
                        atualCliente == null
                        ? "\n"
                        : "\n Cliente: " + atualCliente.getName();
                    prompt +=
                        atualConta == null
                        ? "\n"
                        : "\n Conta: " + String.format("%s [%s]", atualConta.getId(), atualConta.getSaldo());


                    String line = showInputDialog(null, prompt, banco.getName(), 1);
                    if (line.equals("exit")) {
                        break;
                    } else if (line.equals("1")) {
                        // cria cliente
                        atualCliente = createCustomer();
                        banco.addCliente(atualCliente);
                        atualConta = null;
                    } else if (line.equals("2")) {
                        listCustomers();
                    } else if (line.equals("3")) {
                        String id = showInputDialog(null, "codigo do cliente: ");
                        atualCliente = banco.getCliente(id);
                        atualConta = null;
                    } else if (line.equals("4")) {
                        if (atualCliente == null) {
                            throw new BancoException("D05", "cliente não selecionado");
                        }
                        Conta conta = createAccount(atualCliente);
                        atualCliente.addConta(conta);
                        atualConta = conta;
                        // adicionar conta no banco
                        banco.addConta(atualConta);

                    } else if (line.equals("5")) {
                        if (atualCliente == null) {
                            throw new BancoException("D05", "cliente não selecionado");
                        }
                        listAccounts(atualCliente.getContas());

                    } else if (line.equals("6")) {
                        if (atualCliente == null) {
                            throw new BancoException("D05", "cliente não selecionado");
                        }
                        String id = showInputDialog(null, "codigo da conta: ");
                        atualConta = atualCliente.getConta(id);

                    } else if (line.equals("7")) {

                        // depositar
                        if (atualConta == null) {
                            throw new BancoException("D06", "conta não selecionada");
                        }
                        double valor = inputValue();
                        atualConta.depositar(valor);

                    } else if (line.equals("8")) {

                        // sacar
                        if (atualConta == null) {
                            throw new BancoException("D06", "conta não selecionada");
                        }
                        double valor = inputValue();
                        atualConta.sacar(valor);

                    } else if (line.equals("9")) {

                        listAccounts(banco.getContas());

                    } else if (line.equals("10")) {

                        // remover conta do cliente
                        if (atualCliente == null) {
                            throw new BancoException("D05", "cliente não selecionado");
                        }
                        if (atualConta == null) {
                            throw new BancoException("D06", "conta não selecionada");
                        }
                        if(atualConta.getSaldo() > 0) {
                            throw new BancoException("D08", "conta com saldo positivo");
                        }
                        atualCliente.removerConta(atualConta);
                        banco.removerConta(atualConta);
                        atualConta = null;

                    } else if (line.equals("11")) {
                        // remover cliente do banco
                        if (atualCliente == null) {
                            throw new BancoException("D05", "cliente não selecionado");
                        }
                        banco.removerCliente(atualCliente);
                        for (Conta c : atualCliente.getContas()) {
                            if(c.getSaldo() > 0) {
                                throw new BancoException("D08", "conta com saldo positivo: \n" + c.getId());
                            }
                        }
                        for (Conta c : atualCliente.getContas()) {
                            banco.removerConta(c);
                        }
                        atualConta = null;
                        atualCliente = null;

                    } else if (line.equals("r")) {

                        banco.getContas().forEach(c -> {
                            if (c instanceof Rendimento) {
                                ((Rendimento) c).render();
                            }
                        });

                    } else if (line.length() == 0) {
                    } else {
                        throw new UnsupportedOperationException("invalid command");
                    }
                } catch (UnsupportedOperationException e) {
                    showMessageDialog(null, e.getMessage());
                } catch (BancoException e) {
                    showMessageDialog(null, e.getMessage());
                } catch (Exception e) {
                    showMessageDialog(null, e.getStackTrace());
                }
            }
        } catch(Exception e) {
            showMessageDialog(null, e.getStackTrace());
        } finally {
            showMessageDialog(null, "Até logo!");    
        }
    }

    private void listCustomers() {
        // for (Cliente c: banco.getClientes()) {
        //     System.out.println(c);
        // }
        String msg = "Clientes: \n";
        for (Cliente c: banco.getClientes()) {
            msg += c + "\n";
        }
        showMessageDialog(null, msg);
    }

    private void listAccounts(List<Conta> contas) {
        String msg = "Contas: \n";
        for (Conta c: contas) {
            if(c instanceof ContaInvestimento) {
                msg += String.format("CI | %s [%.2f]\n", c.getId(), c.saldo);
            } else if(c instanceof ContaCorrente) {
                    msg += String.format("CC | %s [%.2f]\n", c.getId(), c.saldo);
            } else if(c instanceof ContaPoupanca) {
                    msg += String.format("CP | %s [%.2f]\n", c.getId(), c.saldo);
            }
        }
        showMessageDialog(null, msg);
    }

    private Cliente createCustomer() {

        Cliente cliente;

        String name = showInputDialog(null, "Nome: ");
        if (name == null || name.trim().length() == 0) {
            throw new BancoException("D01", "Nome invalido");
        }


        String tipo = showInputDialog(null, "Tipo Fisica|Juridica [f|j]: ");
        if (tipo == null || tipo.trim().length() == 0) {
            throw new BancoException("D02", "Tipo invalido");
        }

        if (tipo.trim().toLowerCase().equals("f")) {
            String cpf = null;
            while (true) {
                cpf = showInputDialog(null, "CPF: ");
                if (Util.isCpf(cpf)) break;
                showMessageDialog(null, "CPF invalido");
            }
            cliente = new PessoaFisica(name, cpf);
        } else {
            String cnpj = showInputDialog(null, "CNPJ: ");
            if (cnpj == null || cnpj.trim().length() == 0) {
                throw new BancoException("D03", "CNPJ invalido");
            }
            cliente = new PessoaJuridica(name, cnpj);
        }

        // nao eh possivel, pois a classe cliente
        // eh abstrata
        // Cliente cliente = new Cliente(name);

        return cliente;
    }
    

    private Conta createAccount(Cliente cliente) {
        if (cliente == null) {
            throw new RuntimeException("Cliente nao definido");
        }
        Conta conta;
        String tipo = showInputDialog(null, "Tipo [(P)oupanca|(C)orrente|(I)nvestimento]: ");
        if (tipo == null || tipo.trim().length() == 0) {
            throw new BancoException("D04", "Tipo invalido");
        }
        tipo = tipo.trim().toLowerCase();

        if (tipo.equals("p")) {
            conta = new ContaPoupanca(cliente);
        } else if (tipo.equals("c")) {
            conta = new ContaCorrente(cliente);
        } else {
            conta = new ContaInvestimento(cliente);
        }

        return conta;
    }

    private double inputValue() {
        while (true) {
            try {
                String s = showInputDialog(null, "valor: ");
                if (s == null || s.trim().length() == 0) {
                    throw new BancoException("D07", "valor invalido");
                }
                return Double.parseDouble(s);
            } catch (Exception e) {
                showMessageDialog(null, e.getStackTrace());
            }
        }
    }

}
