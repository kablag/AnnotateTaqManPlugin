package com.kablag.annotateTaqMan;

import com.biomatters.geneious.publicapi.plugin.*;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.sequence.SequenceAnnotation;
import com.biomatters.geneious.publicapi.documents.sequence.NucleotideSequenceDocument;
import com.biomatters.geneious.publicapi.documents.sequence.SequenceAnnotationInterval;
import com.biomatters.geneious.publicapi.utilities.SequenceUtilities;
import com.biomatters.geneious.publicapi.plugin.Options;

import jebl.util.ProgressListener;

import java.util.*;

import static com.biomatters.geneious.publicapi.plugin.GeneiousActionOptions.createSubmenuActionOptions;

/**
 * This plugin shows how to create a simple SequenceAnnotationGenerator by providing a simple implementation of a Motif finder.
 * 
 * @author Matt Kearse
 * @version $Id$
 */

public class AnnotateTaqMan extends SequenceAnnotationGenerator {
    public GeneiousActionOptions getActionOptions() {
        return new GeneiousActionOptions("Create TaqMan",
                "Create Annotations for TaqMan Probe").
                setMainMenuLocation(GeneiousActionOptions.MainMenu.AnnotateAndPredict);
    }

    public String getHelp() {
        return "";
    }

    public Options getOptions(AnnotatedPluginDocument[] documents, SelectionRange selectionRange) throws DocumentOperationException {
        return new AnnotateTaqManOptions(); // Provides all the options we display to the user. See AnnotateTaqManOptions below for details.
    }

    public DocumentSelectionSignature[] getSelectionSignatures() {
        return new DocumentSelectionSignature[] {
                new DocumentSelectionSignature(NucleotideSequenceDocument.class,1,1)
                // This indicates this annotation generator will accept a single nucleotide sequence as input  
        };
    }

    public List<List<SequenceAnnotation>> generateAnnotations(
            AnnotatedPluginDocument[] annotatedPluginDocuments,
            SelectionRange selectionRange, ProgressListener progressListener,
            Options _options) throws DocumentOperationException {


        AnnotateTaqManOptions options = (AnnotateTaqManOptions) _options;
        List<List<SequenceAnnotation>> results = new ArrayList<List<SequenceAnnotation>>();

        String[] substring;

        if (options.inputType.getValue().getName() == "inputStandard") {
            substring = options.fqPare.getValue().getLabel().split("-");
        } else {
            substring = options.customFqPare.getValue().split("-");
        }

        String fluor = substring[0];
        String quencher = substring[1];

        for (AnnotatedPluginDocument document: annotatedPluginDocuments) {
            NucleotideSequenceDocument sequence= (NucleotideSequenceDocument) document.getDocument();
            int len = sequence.getSequenceLength();
            List<SequenceAnnotation> subresult = new ArrayList<SequenceAnnotation>();
            subresult.add(new SequenceAnnotation(fluor, "Fluorophore",
                    new SequenceAnnotationInterval(1, 1)));
            subresult.add(new SequenceAnnotation(quencher, "Quencher",
                    new SequenceAnnotationInterval(len, len)));
            results.add(subresult);
        }

        return results;
    }

    private static class AnnotateTaqManOptions extends Options {
        private RadioOption<InputType> inputType;

        public static final InputType inputStandard = new InputType("inputStandard",
                "Standard");
        public static final InputType inputCustom = new InputType("inputCustom",
                "Custom");

        public static final FqPare famRtq1 = new FqPare("famRtq1","FAM-RTQ1");
        public static final FqPare r6gBhq2 = new FqPare("r6gBhq2","R6G-BHQ2");
        public static final FqPare roxBhq2 = new FqPare("roxBhq2","ROX-BHQ2");
        public static final FqPare cy5Bhq2 = new FqPare("cy5Bhq2","Cy5-BHQ2");

        private ComboBoxOption<FqPare> fqPare;

        private  StringOption customFqPare;

        private AnnotateTaqManOptions() {
            inputType = addRadioOption(
                    "inputType",
                    "",
                    Arrays.asList(inputStandard, inputCustom),
                    inputStandard,
                    Alignment.VERTICAL_ALIGN);

            fqPare = addComboBoxOption("fqPare",
                    "FQ Pare",
                    Arrays.asList(famRtq1, r6gBhq2, roxBhq2, cy5Bhq2),
                    famRtq1);

            customFqPare = addStringOption("customFqPare",
                    "Custom Pare",
                    "VIC-BHQ2");

            inputType.addDependent(inputStandard, fqPare, true);
            inputType.addDependent(inputCustom, customFqPare, true);
        }

        public InputType getInputType() {
            return inputType.getValue();
        }

        public FqPare getFqPare() {
            return fqPare.getValue();
        }

        public String getCustomFqPare() {
            return customFqPare.getValue();
        }

        public static final class InputType extends Options.OptionValue {
            public InputType(String name, String label) {
                super(name, label);
            }
        }

        public static final class FqPare extends Options.OptionValue {
            public FqPare(String name, String label) {
                super(name, label);
            }
        }

    }
}
