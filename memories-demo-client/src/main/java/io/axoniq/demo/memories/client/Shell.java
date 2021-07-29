package io.axoniq.demo.memories.client;

import java.io.IOException;
import java.net.URISyntaxException;

import org.jline.builtins.Completers.TreeCompleter;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.ParsedLine;
import org.jline.reader.impl.DefaultParser;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp.Capability;

import io.axoniq.demo.memories.api.Book;
import io.axoniq.demo.memories.api.BooksRepository;

public class Shell {
	
	private Client client = Client.get();

	public static void main(String[] args) throws IOException, URISyntaxException {
		new Shell();
	}

	public Shell() throws IOException, URISyntaxException {
		Terminal terminal = TerminalBuilder.terminal();

		TreeCompleter treeCompleter = new TreeCompleter(
				TreeCompleter.node("soldCopies", TreeCompleter.node(new StringsCompleter(BooksRepository.get().getAllTitles()))),
				TreeCompleter.node("buyBooks"),
				TreeCompleter.node("generateSales"),
				TreeCompleter.node("quit"),
				TreeCompleter.node("exit"),
				TreeCompleter.node("clear")
			);
		
		DefaultParser parser = new DefaultParser();
		parser.setEscapeChars(null);
		parser.setQuoteChars(new char[] {'\''});
		
		LineReader reader = LineReaderBuilder.builder().terminal(terminal).parser(parser).completer(treeCompleter).build();

		String line;
		while (true) {
			line = reader.readLine("Book shopping app -> ").trim();

			if (line.equalsIgnoreCase("quit") || line.equalsIgnoreCase("exit")) {
				break;
			}
			ParsedLine parsedLine = reader.getParser().parse(line, 0);
			String command = parsedLine.words().get(0);
			String param = parsedLine.words().size() > 1 ? parsedLine.words().get(1) : null;

			try {
				switch (command) {
				case "clear":
					terminal.puts(Capability.clear_screen);
					terminal.flush();
					break;
				case "buyBooks":
					if (param == null || param.trim().length() == 0) {
						client.simulateClientShopping();
					} else {
						client.simulateClientShopping(param);
					}
					break;
				case "generateSales":
					client.generate(Integer.parseInt(param));
					break;
				case "soldCopies":
					Book book = BooksRepository.get().get(param).get();
					client.printSoldCopies(book.getId());
					break;
				default:
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		client.shutdown();
	}

}
