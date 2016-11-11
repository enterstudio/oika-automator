package com.pragmasphere.oika.automator.commands.identifiants;

import com.pragmasphere.oika.automator.fluentlenium.data.FicheClient;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

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
        String text = run.getText(0);
        if (text != null && text.contains(marker)) {
            text = text.replace(marker, available.pop());
            run.setText(text, 0);
            return true;
        }
        return false;
    }
}
