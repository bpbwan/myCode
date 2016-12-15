#ifndef __JNICAR__H___
#define __JNICAR__H___


void SaveAsFile(void *argv);
void saveData(int a, int b, char *filename, bool *flag);
int transferMode(int mode);
void isFileOkAndSaveTrack(int mode);

void PrintBcParam(int m);


struct argSaveFile{
	int mode;
	char filename[30];
};
#endif
