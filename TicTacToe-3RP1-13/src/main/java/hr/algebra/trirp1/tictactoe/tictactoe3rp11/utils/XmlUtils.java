package hr.algebra.trirp1.tictactoe.tictactoe3rp11.utils;

import hr.algebra.trirp1.tictactoe.tictactoe3rp11.exception.XmlParseException;
import hr.algebra.trirp1.tictactoe.tictactoe3rp11.model.GameMove;
import hr.algebra.trirp1.tictactoe.tictactoe3rp11.model.GameMoveTag;
import hr.algebra.trirp1.tictactoe.tictactoe3rp11.model.Position;
import hr.algebra.trirp1.tictactoe.tictactoe3rp11.model.Symbol;
import org.w3c.dom.*;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class XmlUtils {

    private XmlUtils() {}

    private static final String DOCTYPE = "DOCTYPE";
    private static final String DTD = "dtd/gameMoves.dtd";
    private static final String GAME_MOVES = "GameMoves";
    private static final String FILENAME = "xml/gameMoves.xml";

    public static void saveNewMove(GameMove gameMove)
    {
        List<GameMove> gameMoveList = null;
        try {
            gameMoveList = loadGameMoves();
            Document document = createDocument(GAME_MOVES);

            if(gameMoveList.isEmpty()) {
                appendGameMoveElement(gameMove, document);
            }
            else {
                gameMoveList.add(gameMove);
                for(GameMove nextGameMove : gameMoveList) {
                    appendGameMoveElement(nextGameMove, document);
                }
            }

            saveDocument(document, FILENAME);

        } catch (ParserConfigurationException | TransformerException e) {
            throw new XmlParseException("An error occured while saving XML move", e);
        }

    }

    public static List<GameMove> loadGameMoves() {
        return parse(FILENAME);
    }

    private static List<GameMove> parse(String path) {

        if(!Files.exists(Path.of(path))) {
            return new ArrayList<>();
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(true);
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new XmlParseException("An error occured while parsing game moves!", e);
        }
        builder.setErrorHandler(new ErrorHandler() {
            @Override
            public void warning(SAXParseException exception) throws SAXException {
                throw new XmlParseException("A warning occured while building XML with game moves!", exception);
            }

            @Override
            public void error(SAXParseException exception) throws SAXException {
                throw new XmlParseException("An error occured while building XML with game moves!", exception);
            }

            @Override
            public void fatalError(SAXParseException exception) throws SAXException {
                throw new XmlParseException("A fatal error occured while building XML with game moves!", exception);
            }
        });
        Document document = null;
        try {
            document = builder.parse(new File(path));
        } catch (SAXException | IOException e) {
            throw new XmlParseException("An error occured while parsing games moves in XML format!", e);
        }
        return retrieveEmployees(document);
    }

    private static List<GameMove> retrieveEmployees(Document document) {
        List<GameMove> gameMoves = new ArrayList<>();
        Element documentElement = document.getDocumentElement();
        NodeList nodes = documentElement.getElementsByTagName(GameMoveTag.GAME_MOVE.getTagName());
        for (int i = 0; i < nodes.getLength(); i++) {
            GameMove gameMove = new GameMove();
            Element item = (Element) nodes.item(i);
            gameMove.setSymbol(Symbol.valueOf(
                    item.getElementsByTagName(
                            GameMoveTag.SYMBOL.getTagName()).item(0).getTextContent()));
            Position gameMovePosition = new Position();
            gameMovePosition.setColumn(Integer.parseInt(
                    item.getElementsByTagName(
                            GameMoveTag.POSITION_X.getTagName()).item(0).getTextContent()));
            gameMovePosition.setRow(Integer.parseInt(
                    item.getElementsByTagName(
                            GameMoveTag.POSITION_Y.getTagName()).item(0).getTextContent()));
            gameMove.setPosition(gameMovePosition);
            gameMoves.add(gameMove);
        }

        return gameMoves;
    }

    private static Document createDocument(String element) throws ParserConfigurationException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        DOMImplementation dom = builder.getDOMImplementation();
        DocumentType docType = dom.createDocumentType(DOCTYPE, null, DTD);
        return dom.createDocument(null, element, docType);
    }

    private static void saveDocument(Document document, String filename) throws TransformerException {
        Transformer transformer = TransformerFactory.newDefaultInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, document.getDoctype().getSystemId());
        transformer.transform(new DOMSource(document), new StreamResult(new File(filename)));
    }

    private static void appendGameMoveElement(GameMove gameMove, Document document) {
        Element element = document.createElement(GameMoveTag.GAME_MOVE.getTagName());
        document.getDocumentElement().appendChild(element);

        element.appendChild(createElement(document, GameMoveTag.SYMBOL.getTagName(), gameMove.getSymbol().name()));
        element.appendChild(createElement(document, GameMoveTag.POSITION_X.getTagName(),
                gameMove.getPosition().getColumn().toString()));
        element.appendChild(createElement(document, GameMoveTag.POSITION_Y.getTagName(),
                gameMove.getPosition().getRow().toString()));
    }

    private static Node createElement(Document document, String tagName, String data) {
        Element element = document.createElement(tagName);
        Text text = document.createTextNode(data);
        element.appendChild(text);
        return element;
    }

}
