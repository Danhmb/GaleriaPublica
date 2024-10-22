package haddad.maia.barbosa.galeriapublica;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GalleryRepository {
    public class GalleryRepository {

        // Contexto da aplicação
        Context context;

        // Construtor que recebe o contexto
        public GalleryRepository(Context context) {
            this.context = context;
        }

        // Método para carregar dados das imagens
        public List<ImageData> loadImageData(Integer limit, Integer offSet) throws FileNotFoundException {
            List<ImageData> imageDataList = new ArrayList<>();

            // Obtém as dimensões da imagem a partir dos recursos
            int w = (int) context.getResources().getDimension(R.dimen.im_width);
            int h = (int) context.getResources().getDimension(R.dimen.im_height);

            // Define as colunas a serem carregadas
            String[] projection = new String[] {
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.DATE_ADDED,
                    MediaStore.Images.Media.SIZE
            };

            String selection = null;
            String selectionArgs[] = null;
            String sort = MediaStore.Images.Media.DATE_ADDED;

            Cursor cursor = null;

            // Verifica a versão do Android para construir a consulta
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                Bundle queryArgs = new Bundle();

                // Adiciona parâmetros à consulta
                queryArgs.putString(ContentResolver.QUERY_ARG_SQL_SELECTION, selection);
                queryArgs.putStringArray(ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS, selectionArgs);
                queryArgs.putString(ContentResolver.QUERY_ARG_SORT_COLUMNS, sort);
                queryArgs.putInt(ContentResolver.QUERY_ARG_SORT_DIRECTION, ContentResolver.QUERY_SORT_DIRECTION_ASCENDING);
                queryArgs.putInt(ContentResolver.QUERY_ARG_LIMIT, limit);
                queryArgs.putInt(ContentResolver.QUERY_ARG_OFFSET, offSet);

                // Executa a consulta
                cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        projection, queryArgs, null);
            } else {
                // Para versões anteriores do Android
                cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        projection, selection, selectionArgs, sort + " ASC LIMIT " + limit + " OFFSET " + offSet);
            }

            // Obtém os índices das colunas de interesse
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            int nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
            int dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED);
            int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE);

            // Processa os resultados do cursor
            while (cursor.moveToNext()) {
                // Extrai os valores das colunas
                long id = cursor.getLong(idColumn);
                Uri contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                String name = cursor.getString(nameColumn);
                int dateAdded = cursor.getInt(dateAddedColumn);
                int size = cursor.getInt(sizeColumn);

                // Obtém a miniatura da imagem
                Bitmap thumb = Util.getBitmap(context, contentUri, w, h);

                // Cria um objeto ImageData e adiciona à lista
                imageDataList.add(new ImageData(contentUri, thumb, name, new Date(dateAdded * 1000L), size));
            }

            // Retorna a lista de dados das imagens
            return imageDataList;
        }
    }

}
