import { Component, Input } from '@angular/core';
import { FileInfoEntity } from '../../../models/folder.model';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-file-properties',
  imports: [],
  templateUrl: './file-properties.component.html',
  styleUrl: './file-properties.component.css'
})
export class FilePropertiesComponent {
  @Input() FileInfo: FileInfoEntity = {
    FileId: '',
    OriginalName: '',
    FileExtension: '',
    FileSize: 0,
    CreatedAt: '',
    UploadedAgo: ''
  };

  File: FileInfoEntity = {
    FileId: '',
    OriginalName: '',
    FileExtension: '',
    FileSize: 0,
    CreatedAt: '',
    UploadedAgo: ''
  };

  constructor(private http: HttpClient) { }

  ngOnInit(): void {
    this.File = this.FileInfo;
    console.log('File Properties Component Initialized with File Info:', this.File);
  }

  ViewDetails() {

  }

  DownloadFile(): void {
    window.open(
      `http://localhost:8080/api/file/download/download/1e9041ec-5aa0-4ab9-aaea-6e87ac08db6e`,
      "_blank"
    );

    // this.http.get(
    //   `http://localhost:8080/api/file/download/download/1e9041ec-5aa0-4ab9-aaea-6e87ac08db6e`,
    //   {
    //     responseType: 'blob',
    //     observe: 'response'
    //   }
    // ).subscribe(response => {

    //   const blob = response.body!;

    //   const disposition = response.headers.get('Content-Disposition');

    //   let filename = 'download';

    //   if (disposition) {
    //     const match = disposition.match(/filename="?(.+?)"?$/);
    //     if (match) {
    //       filename = match[1];
    //     }
    //   }


    //   const url = window.URL.createObjectURL(blob);

    //   const a = document.createElement('a');
    //   a.href = url;
    //   a.download = filename;
    //   a.click();

    //   URL.revokeObjectURL(url);
    // });
  }

  RenameFile() {

  }

  DeleteFile() {

  }
}
