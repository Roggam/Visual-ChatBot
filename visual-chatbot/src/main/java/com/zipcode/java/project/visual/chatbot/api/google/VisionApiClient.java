package com.zipcode.java.project.visual.chatbot.api.google;

import com.google.cloud.spring.vision.CloudVisionTemplate;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.Feature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@Component
public class VisionApiClient {

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private CloudVisionTemplate cloudVisionTemplate;

    public String getLabelDetection() {
        String imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b5/Lion_d%27Afrique.jpg/290px-Lion_d%27Afrique.jpg";
        Resource imageResource = this.resourceLoader.getResource(imageUrl);
        AnnotateImageResponse response = this.cloudVisionTemplate.analyzeImage(imageResource,
                Feature.Type.LABEL_DETECTION);
        return response.getLabelAnnotationsList().toString();

    }

    public String getLandmarkDetection() {
        String imageUrl = "https://www.parisinfo.com/var/otcp/sites/images/node_43/node_51/node_77884/node_77888/tour-eiffel-tour-eiffel-illumin%C3%A9e-depuis-champs-de-mars-%C3%A9clairage-dor%C3%A9-%7C-630x405-%7C-%C2%A9-sete-emeric-livinec/21230551-1-fre-FR/Tour-Eiffel-Tour-Eiffel-illumin%C3%A9e-depuis-Champs-de-Mars-%C3%A9clairage-dor%C3%A9-%7C-630x405-%7C-%C2%A9-SETE-Emeric-Livinec.jpg";
        Resource imageResource = this.resourceLoader.getResource(imageUrl);
        AnnotateImageResponse response = this.cloudVisionTemplate.analyzeImage(imageResource,
                Feature.Type.LANDMARK_DETECTION);

        return response.getLandmarkAnnotationsList().toString();
    }

    public String extract() {
        String imageUrl = "https://cloud.google.com/vision/docs/images/sign_text.png";
        return this.cloudVisionTemplate.extractTextFromImage(this.resourceLoader.getResource(imageUrl));
    }

    public String analyzeImage(Resource imageResource) {
        AnnotateImageResponse response = this.cloudVisionTemplate.analyzeImage(
                                                    imageResource,
                                                    Feature.Type.LANDMARK_DETECTION,
                                                    Feature.Type.LABEL_DETECTION,
                                                    Feature.Type.FACE_DETECTION,
                                                    Feature.Type.LOGO_DETECTION,
                                                    Feature.Type.PRODUCT_SEARCH,
                                                    Feature.Type.SAFE_SEARCH_DETECTION,
                                                    Feature.Type.WEB_DETECTION,
                                                    Feature.Type.OBJECT_LOCALIZATION,
                                                    Feature.Type.CROP_HINTS,
                                                    Feature.Type.TEXT_DETECTION);

        StringBuilder labelsDetected = new StringBuilder();

        if (response.getWebDetection().getBestGuessLabelsCount() > 0) {
            labelsDetected.append("Best Web match ").append(response.getWebDetection().getBestGuessLabels(0).getLabel()).append("\n");
        }
        if (response.getLabelAnnotationsCount() > 0) {
            labelsDetected.append("Best Label match ");
            response.getLabelAnnotationsList().stream().limit(3).map(l -> l.getDescription()).forEach(ans -> labelsDetected.append(ans).append("\n"));
        }
        if (response.getTextAnnotationsCount() > 0) {
            labelsDetected.append("Best Text match ");
            response.getTextAnnotationsList().stream().filter(l -> l.getLocale() != null).map(l -> l.getDescription()).forEach(ans -> labelsDetected.append(ans).append("\n"));
        }
        if (response.getLandmarkAnnotationsCount() > 0) {
            labelsDetected.append("Best Landmark match ");
            response.getLandmarkAnnotationsList().stream().limit(5).map(l -> l.getDescription()).forEach(ans -> labelsDetected.append(ans).append("\n"));
        }
        if (response.getFaceAnnotationsCount() > 0) {
            labelsDetected.append("Best Face match ");
            response.getFaceAnnotationsList().stream().limit(3).map(l -> l.getAngerLikelihood().name() + " " + l.getJoyLikelihood().name()).forEach(ans -> labelsDetected.append(ans).append("\n"));
        }
        if (response.getLogoAnnotationsCount() > 0) {
            labelsDetected.append("Best Logo match ");
            response.getLogoAnnotationsList().stream().limit(3).map(l -> l.getDescription()).forEach(ans -> labelsDetected.append(ans).append("\n"));
        }
        return labelsDetected.toString();
    }
}
