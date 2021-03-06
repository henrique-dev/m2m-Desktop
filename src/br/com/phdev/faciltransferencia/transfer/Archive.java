/*
 * Copyright (C) 2018 Paulo Henrique Gonçalves Bacelar
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package br.com.phdev.faciltransferencia.transfer;

import java.io.Serializable;

/**
 *
 * @author Paulo Henrique Gonçalves Bacelar
 */
public class Archive {    

    private String name;
    private String path;
    private String masterPath;
    private String localPath;
    private int statusTransfer = 0;
    private byte[] bytes;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
    
    public void setStatusTranfer(int status) {
        this.statusTransfer = status;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public int getStatusTransfer() {
        return statusTransfer;
    }

    public void setStatusTransfer(int statusTransfer) {
        this.statusTransfer = statusTransfer;
    }        

    public String getMasterPath() {
        return masterPath;
    }

    public void setMasterPath(String masterPath) {
        this.masterPath = masterPath;
    }        

    @Override
    public String toString() {
        switch (statusTransfer) {
            case 0:
                return "AGUARDANDO PARA ENVIAR! | " + this.name;
            case 1:
                return "ENVIANDO... | " + this.name;
            case 2:
                return "ENVIO COMPLETO! | " + this.name;
            case 3:
                return "FALHA NO ENVIO - ESPAÇO INSUFICIENTE! | " + this.name;
            default:
                return "Erro no arquivo";
        }        
    }

}
