package org.telosys.tools.eclipse.plugin.editors.dbrep;

import java.io.File;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;
import org.telosys.tools.api.GenericModelLoader;
import org.telosys.tools.commons.TelosysToolsException;
import org.telosys.tools.eclipse.plugin.commons.ModelUtil;
import org.telosys.tools.eclipse.plugin.commons.MsgBox;
import org.telosys.tools.eclipse.plugin.editors.commons.AbstractModelEditor;
import org.telosys.tools.generic.model.Model;
import org.telosys.tools.repository.model.RepositoryModel;
import org.telosys.tools.repository.persistence.PersistenceManager;
import org.telosys.tools.repository.persistence.PersistenceManagerFactory;

/**
 * Main entry point for the "Database Model" editor <br>
 * This editor contains 4 pages : <br>
 * . Entities attributes and mapping (table view) <br>
 * . Links between entities <br>
 * . Information <br>
 * . Code generation <br>
 * 
 */
public class RepositoryEditor extends AbstractModelEditor
{
	//========================================================================================
	// Editor plugin startup ( for each file to edit ) :
	// Step 1 : init()
	// Step 2 : addPages()
	//========================================================================================

    @Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.init(site, input);
		log(this, "init()..." );
		loadModel();
	}
	
    //----------------------------------------------------------------------------------------
	@Override
	protected void addPages() {
		log(this, "addPages()..." );

		IFormPage page1 = new RepositoryEditorPageModelEntities(this, 
								"RepositoryEditorPageId1", 
								" Model : Entities attributes and mapping "  );
		
		IFormPage page2 = new RepositoryEditorPageModelLinks(this, 
								"RepositoryEditorPageId2", 
								" Model : Links between entities ");
		
		IFormPage page3 = new RepositoryEditorPageModelInfo(this, 
								"RepositoryEditorPageId3", 
								" Model : Information " );
		
		IFormPage page4 = new RepositoryEditorPageCodeGeneration(this, 
								"RepositoryEditorPageId4", 
								" Code generation " );
		
		try {
			addPage(page1);
			addPage(page2);
			addPage(page3);
			addPage(page4);
			log(this, "addPages() : all pages added" );
		} catch (PartInitException e) {
			MsgBox.error("RepositoryEditor : addPage(page) throws PartInitException ", e);
		}		
	}

    //----------------------------------------------------------------------------------------
    @Override
    protected Model loadModel(File modelFile) {
		log(this, "loadModel(" + modelFile + ")");
		GenericModelLoader genericModelLoader = new GenericModelLoader( getProjectConfig() ) ;
		try {
			Model model = genericModelLoader.loadModel(modelFile);
			//log("loadModel() : done. Model name = " + model.getName() + " - " + model.getEntities().size() + " entities");
			return model;
		} catch (Exception ex) {
			MsgBox.error("Cannot load model ", ex);
			return null ;
		}    	
    }
    
    //----------------------------------------------------------------------------------------
	@Override
    public void saveModel( Model model, File modelFile ) {
		log(this, "saveModel(" + modelFile + ")");
		RepositoryModel repositoryModel;
		try {
			repositoryModel = ModelUtil.toRepositoryModel( model );
		} catch (Exception e1) {
			MsgBox.error("Cannot save model.\n Cannot convert the given model to RepositoryModel");
			return;
		}
		
		PersistenceManager persistenceManager = PersistenceManagerFactory.createPersistenceManager(modelFile, getLogger());
		try {
			persistenceManager.save(repositoryModel);
			log(this, "Repository saved.");
		} catch (TelosysToolsException e) {
			MsgBox.error("Cannot save repository", e);
		}
    }

}