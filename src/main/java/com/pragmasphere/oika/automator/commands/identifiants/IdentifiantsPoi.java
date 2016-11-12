package com.pragmasphere.oika.automator.commands.identifiants;

import com.pragmasphere.oika.automator.fluentlenium.data.FicheClient;
import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class IdentifiantsPoi {

    /**
     * Renseigne les clients dans le document
     *
     * @param doc     document (docx)
     * @param clients clients à renseigner
     * @return nombre de clients renseignés.
     */
    public static int fillClient(final XWPFDocument doc, final List<FicheClient> clients) {

        final LinkedList<String> noms = clients.stream().map(c -> c.getNom() + " " + c.getPrenom())
                .collect(Collectors.toCollection(() -> new LinkedList<>()));
        final LinkedList<String> identifiants = clients.stream().map(c -> c.getId())
                .collect(Collectors.toCollection(() -> new LinkedList<>()));
        final LinkedList<String> passwords = clients.stream().map(c -> c.getPassword())
                .collect(Collectors.toCollection(() -> new LinkedList<>()));

        for (final XWPFParagraph p : doc.getParagraphs()) {
            final List<XWPFRun> runs = p.getRuns();
            if (runs != null) {
                for (final XWPFRun r : runs) {
                    replace(r, noms, identifiants, passwords);
                }
            }
        }

        for (final XWPFTable tbl : doc.getTables()) {
            for (final XWPFTableRow row : tbl.getRows()) {
                for (final XWPFTableCell cell : row.getTableCells()) {
                    for (final XWPFParagraph p : cell.getParagraphs()) {
                        for (final XWPFRun r : p.getRuns()) {
                            replace(r, noms, identifiants, passwords);
                        }
                    }
                }
            }
        }

        if (noms.size() != identifiants.size() || identifiants.size() != passwords.size() || noms.size() != passwords.size()) {
            throw new IllegalStateException("Problème dans le modèle Word des identifiants");
        }

        return clients.size() - noms.size();
    }

    private static void replace(final XWPFRun r, final LinkedList<String> noms, final LinkedList<String> identifiants,
            final LinkedList<String> passwords) {
        replace(r, "%NOM%", noms);
        replace(r, "%IDENTIFIANT%", identifiants);
        replace(r, "%MDP%", passwords);
    }

    private static boolean replace(final XWPFRun run, final String marker, final LinkedList<String> available) {
        if (available.size() == 0) {
            return false;
        }
        String text = run.getText(0);
        if (text != null && text.contains(marker)) {
            text = text.replace(marker, available.pop());
            run.setText(text, 0);
            return true;
        }
        return false;
    }

    public static final void appendTemplate(final XWPFDocument doc, final XWPFDocument templateDoc) {
        final XWPFParagraph paragraph = doc.createParagraph();
        final XWPFRun run = paragraph.createRun();
        run.addBreak(BreakType.PAGE);
        appendBody(doc.getDocument().getBody(), templateDoc.getDocument().getBody());
    }

    private static void appendBody(final CTBody src, final CTBody append) {
        final XmlOptions optionsOuter = new XmlOptions();
        optionsOuter.setSaveOuter();
        final String appendString = append.xmlText(optionsOuter);
        final String srcString = src.xmlText();
        final String prefix = srcString.substring(0, srcString.indexOf(">") + 1);
        final String mainPart = srcString.substring(srcString.indexOf(">") + 1, srcString.lastIndexOf("<"));
        final String sufix = srcString.substring(srcString.lastIndexOf("<"));
        final String addPart = appendString.substring(appendString.indexOf(">") + 1, appendString.lastIndexOf("<"));
        final CTBody makeBody;
        try {
            makeBody = CTBody.Factory.parse(prefix + mainPart + addPart + sufix);
        } catch (final XmlException e) {
            throw new IllegalStateException(e);
        }
        src.set(makeBody);
    }
}
