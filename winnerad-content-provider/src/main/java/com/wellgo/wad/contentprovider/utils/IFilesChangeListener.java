package com.wellgo.wad.contentprovider.utils;



import java.io.File;

public interface IFilesChangeListener {

	void onFileModify(File file);

	void onFileCreate(File file);

	void onFileDelete(File file);

}
