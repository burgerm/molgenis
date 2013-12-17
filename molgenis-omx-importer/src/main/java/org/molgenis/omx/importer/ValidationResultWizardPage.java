package org.molgenis.omx.importer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.molgenis.data.DataService;
import org.molgenis.data.DatabaseAction;
import org.molgenis.data.support.QueryImpl;
import org.molgenis.framework.db.EntitiesImporter;
import org.molgenis.framework.db.EntityImportReport;
import org.molgenis.omx.observ.DataSet;
import org.molgenis.ui.wizard.AbstractWizardPage;
import org.molgenis.ui.wizard.Wizard;
import org.molgenis.util.ApplicationContextProvider;
import org.molgenis.util.DataSetImportedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

@Component
public class ValidationResultWizardPage extends AbstractWizardPage
{
	private static final long serialVersionUID = 1L;

	private static final Logger logger = Logger.getLogger(ValidationResultWizardPage.class);
	private final DataService dataService;
	private final EntitiesImporter entitiesImporter;
	private final DataSetImporterService dataSetImporterService;

	@Autowired
	public ValidationResultWizardPage(DataService dataService, EntitiesImporter entitiesImporter,
			DataSetImporterService dataSetImporterService)
	{
		this.dataService = dataService;
		this.entitiesImporter = entitiesImporter;
		this.dataSetImporterService = dataSetImporterService;
		if (dataService == null) throw new IllegalArgumentException("DataService is null");
		if (entitiesImporter == null) throw new IllegalArgumentException("EntitiesImporter is null");
		if (dataSetImporterService == null) throw new IllegalArgumentException("DataSetImporterService is null");
	}

	@Override
	public String getTitle()
	{
		return "Validation";
	}

	@Override
	public String handleRequest(HttpServletRequest request, BindingResult result, Wizard wizard)
	{
		if (!(wizard instanceof ImportWizard))
		{
			throw new RuntimeException("Wizard must be of type '" + ImportWizard.class.getSimpleName()
					+ "' instead of '" + wizard.getClass().getSimpleName() + "'");
		}

		ImportWizard importWizard = (ImportWizard) wizard;
		String entityImportOption = importWizard.getEntityImportOption();

		if (entityImportOption != null)
		{
			return doImport(entityImportOption, result, importWizard);
		}

		return null;
	}

	private String doImport(String entityAction, BindingResult result, ImportWizard importWizard)
	{

		File file = importWizard.getFile();
		try
		{
			// convert input to database action
			DatabaseAction entityDbAction = toDatabaseAction(entityAction);
			if (entityDbAction == null) throw new IOException("unknown database action: " + entityAction);

			// import entities
			EntityImportReport importReport = entitiesImporter.importEntities(file, entityDbAction);
			importWizard.setImportResult(importReport);

			// import dataset instances
			if (importWizard.getDataImportable() != null)
			{
				List<String> dataSetSheetNames = new ArrayList<String>();
				for (Entry<String, Boolean> entry : importWizard.getDataImportable().entrySet())
					if (entry.getValue() == true) dataSetSheetNames.add("dataset_" + entry.getKey());

				dataSetImporterService.importDataSet(file, dataSetSheetNames);
			}

			// publish dataset imported event(s)

			Iterable<DataSet> dataSets = dataService.findAll(DataSet.ENTITY_NAME, new QueryImpl());
			for (DataSet dataSet : dataSets)
				ApplicationContextProvider.getApplicationContext().publishEvent(
						new DataSetImportedEvent(this, dataSet.getId()));

			return "File successfully imported.";

		}
		catch (Exception e)
		{
			logger.warn("Import of file [" + file.getName() + "] failed for action [" + entityAction + "]", e);
			result.addError(new ObjectError("wizard", "<b>Your import failed:</b><br />" + e.getMessage()));
		}

		return null;
	}

	private DatabaseAction toDatabaseAction(String actionStr)
	{
		// convert input to database action
		DatabaseAction dbAction;

		if (actionStr.equals("add")) dbAction = DatabaseAction.ADD;
		else if (actionStr.equals("add_ignore")) dbAction = DatabaseAction.ADD_IGNORE_EXISTING;
		else if (actionStr.equals("add_update")) dbAction = DatabaseAction.ADD_UPDATE_EXISTING;
		else if (actionStr.equals("update")) dbAction = DatabaseAction.UPDATE;
		else if (actionStr.equals("update_ignore")) dbAction = DatabaseAction.UPDATE_IGNORE_MISSING;
		else dbAction = null;

		return dbAction;
	}

}
