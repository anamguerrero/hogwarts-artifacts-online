package edu.tcu.cs.hogwartsartifactsonline.wizard;

import edu.tcu.cs.hogwartsartifactsonline.artifact.Artifact;
import edu.tcu.cs.hogwartsartifactsonline.artifact.ArtifactNotFoundException;
import edu.tcu.cs.hogwartsartifactsonline.artifact.ArtifactRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class WizardService {
    private final WizardRepository wizardRepository;
    private final ArtifactRepository artifactRepository;

    public WizardService(WizardRepository wizardRepository, ArtifactRepository artifactRepository) {
        this.wizardRepository = wizardRepository;
        this.artifactRepository = artifactRepository;
    }

    public Wizard findById(Integer wizardId) {
        return this.wizardRepository.findById(wizardId)
                .orElseThrow(() -> new WizardNotFoundException("" + wizardId));
    }

    public List<Wizard> findAll() {
        return this.wizardRepository.findAll();
    }

    public Wizard save(Wizard newWizard) {
       return this.wizardRepository.save(newWizard);
    }

    public Wizard update(Integer wizardId, Wizard update) {
        return this.wizardRepository.findById(wizardId)
                .map(oldWizard -> {
                    oldWizard.setName(update.getName());
                    return this.wizardRepository.save(oldWizard);
                })
                .orElseThrow(() -> new WizardNotFoundException("" + wizardId));
    }

    public void delete(Integer wizardId) {
        Wizard wizardToBeDeleted = this.wizardRepository.findById(wizardId)
                .orElseThrow(() -> new WizardNotFoundException("" + wizardId));

        //this.wizardRepository.deleteById(wizardId);

        //maybe add more???
        //before deletion, unassign this wizard's owned artifact
        wizardToBeDeleted.removeAllArtifacts();
        this.wizardRepository.deleteById(wizardId);
    }

    /* need to create an assign method?????
            {
					"name": "Assign artifact",
					"request": {
						"method": "PUT",
						"header": [],
						"url": {
							"raw": "localhost:8080/api/v1/wizards/3/artifacts/1250808601744904192",
							"host": [
								"localhost"
							],
							"port": "8080",
							"path": [
								"api",
								"v1",
								"wizards",
								"3",
								"artifacts",
								"1250808601744904192"
							]
						}
					},
					"response": []
				}
    */

    public void assignArtifact(Integer wizardId, String artifactId){
        //find this artifact by Id from DB (database)
        Artifact artifactToBeAssigned = this.artifactRepository.findById(artifactId)
                .orElseThrow(() -> new ArtifactNotFoundException(artifactId));

        //find this wizard by Id from DB
        Wizard wizard = this.wizardRepository.findById(wizardId)
                .orElseThrow(() -> new WizardNotFoundException("" + wizardId));

        //artifact assignment
        if (artifactToBeAssigned.getOwner() != null) {
            artifactToBeAssigned.getOwner().removeArtifact(artifactToBeAssigned);
        }
        wizard.addArtifact(artifactToBeAssigned);
    }
}