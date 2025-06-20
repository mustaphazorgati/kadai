/*
 * Copyright [2025] [envite consulting GmbH]
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 *
 */

import { Component, ElementRef, inject, Input, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { Observable, Subject } from 'rxjs';

import { WorkbasketSummaryRepresentation } from 'app/shared/models/workbasket-summary-representation';
import { WorkbasketSummary } from 'app/shared/models/workbasket-summary';
import { Direction, Sorting, WorkbasketQuerySortParameter } from 'app/shared/models/sorting';

import { WorkbasketService } from 'app/shared/services/workbasket/workbasket.service';
import { OrientationService } from 'app/shared/services/orientation/orientation.service';
import { ImportExportService } from 'app/administration/services/import-export.service';
import { Actions, ofActionCompleted, ofActionDispatched, Store } from '@ngxs/store';
import { takeUntil } from 'rxjs/operators';
import {
  DeselectWorkbasket,
  GetWorkbasketsSummary,
  SelectWorkbasket
} from '../../../shared/store/workbasket-store/workbasket.actions';
import { WorkbasketSelectors } from '../../../shared/store/workbasket-store/workbasket.selectors';
import { Workbasket } from '../../../shared/models/workbasket';
import { MatListOption, MatSelectionList } from '@angular/material/list';
import { DomainService } from '../../../shared/services/domain/domain.service';
import { RequestInProgressService } from '../../../shared/services/request-in-progress/request-in-progress.service';
import { WorkbasketQueryFilterParameter } from '../../../shared/models/workbasket-query-filter-parameter';
import { QueryPagingParameter } from '../../../shared/models/query-paging-parameter';
import { FilterSelectors } from '../../../shared/store/filter-store/filter.selectors';
import { WorkbasketListToolbarComponent } from '../workbasket-list-toolbar/workbasket-list-toolbar.component';
import { AsyncPipe } from '@angular/common';
import { IconTypeComponent } from '../type-icon/icon-type.component';
import { MatDivider } from '@angular/material/divider';
import { PaginationComponent } from '../../../shared/components/pagination/pagination.component';

@Component({
  selector: 'kadai-administration-workbasket-list',
  templateUrl: './workbasket-list.component.html',
  styleUrls: ['./workbasket-list.component.scss'],
  imports: [
    WorkbasketListToolbarComponent,
    MatSelectionList,
    MatListOption,
    IconTypeComponent,
    MatDivider,
    PaginationComponent,
    AsyncPipe
  ]
})
export class WorkbasketListComponent implements OnInit, OnDestroy {
  selectedId = '';
  type = 'workbaskets';
  workbasketDefaultSortBy: WorkbasketQuerySortParameter = WorkbasketQuerySortParameter.NAME;
  sort: Sorting<WorkbasketQuerySortParameter> = {
    'sort-by': this.workbasketDefaultSortBy,
    order: Direction.ASC
  };
  filterBy: WorkbasketQueryFilterParameter = {};
  pageParameter: QueryPagingParameter = {
    page: 1,
    'page-size': 9
  };
  requestInProgress: boolean;
  requestInProgressLocal = false;
  resetPagingSubject = new Subject<null>();
  @Input() expanded: boolean;
  workbasketsSummary$: Observable<WorkbasketSummary[]> = inject(Store).select(WorkbasketSelectors.workbasketsSummary);
  workbasketsSummaryRepresentation$: Observable<WorkbasketSummaryRepresentation> = inject(Store).select(
    WorkbasketSelectors.workbasketsSummaryRepresentation
  );
  selectedWorkbasket$: Observable<Workbasket> = inject(Store).select(WorkbasketSelectors.selectedWorkbasket);
  getWorkbasketListFilter$: Observable<WorkbasketQueryFilterParameter> = inject(Store).select(
    FilterSelectors.getWorkbasketListFilter
  );
  destroy$ = new Subject<void>();
  @ViewChild('workbasket') workbasketList: MatSelectionList;
  private store = inject(Store);
  private workbasketService = inject(WorkbasketService);
  private orientationService = inject(OrientationService);
  private importExportService = inject(ImportExportService);
  private domainService = inject(DomainService);
  private requestInProgressService = inject(RequestInProgressService);
  private ngxsActions$ = inject(Actions);
  @ViewChild('wbToolbar', { static: true })
  private toolbarElement: ElementRef;

  constructor() {
    this.ngxsActions$.pipe(ofActionDispatched(GetWorkbasketsSummary), takeUntil(this.destroy$)).subscribe(() => {
      this.requestInProgressService.setRequestInProgress(true);
      this.requestInProgressLocal = true;
    });
    this.ngxsActions$.pipe(ofActionCompleted(GetWorkbasketsSummary), takeUntil(this.destroy$)).subscribe(() => {
      this.requestInProgressService.setRequestInProgress(false);
      this.requestInProgressLocal = false;
    });
  }

  ngOnInit() {
    this.requestInProgressService.setRequestInProgress(true);
    this.selectedWorkbasket$.pipe(takeUntil(this.destroy$)).subscribe((selectedWorkbasket) => {
      if (typeof selectedWorkbasket !== 'undefined') {
        this.selectedId = selectedWorkbasket.workbasketId;
      } else {
        this.selectedId = undefined;
      }
    });

    this.workbasketService
      .workbasketSavedTriggered()
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        this.performRequest();
      });

    this.orientationService
      .getOrientation()
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        this.refreshWorkbasketList();
      });

    this.importExportService
      .getImportingFinished()
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        this.refreshWorkbasketList();
      });

    this.domainService
      .getSelectedDomain()
      .pipe(takeUntil(this.destroy$))
      .subscribe((domain) => {
        this.filterBy.domain = [domain];
        this.performRequest();
      });

    this.workbasketService
      .getWorkbasketActionToolbarExpansion()
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        this.requestInProgressService.setRequestInProgress(true);
        setTimeout(() => {
          this.refreshWorkbasketList();
        }, 1);
      });

    this.requestInProgressService
      .getRequestInProgress()
      .pipe(takeUntil(this.destroy$))
      .subscribe((value) => {
        this.requestInProgress = value;
      });

    this.getWorkbasketListFilter$.pipe(takeUntil(this.destroy$)).subscribe((filter) => {
      this.performFilter(filter);
    });
  }

  selectWorkbasket(id: string) {
    this.requestInProgressService.setRequestInProgress(true);
    if (this.selectedId === id) {
      this.store
        .dispatch(new DeselectWorkbasket())
        .subscribe(() => this.requestInProgressService.setRequestInProgress(false));
    } else {
      this.store
        .dispatch(new SelectWorkbasket(id))
        .subscribe(() => this.requestInProgressService.setRequestInProgress(false));
    }
  }

  performSorting(sort: Sorting<WorkbasketQuerySortParameter>) {
    this.sort = sort;
    this.performRequest();
  }

  performFilter(filterBy: WorkbasketQueryFilterParameter) {
    const domain = this.filterBy.domain;
    this.filterBy = { ...filterBy };
    this.filterBy.domain = domain;
    this.resetPagingSubject.next(null);
    // this.performRequest();
  }

  changePage(page) {
    this.pageParameter.page = page;
    this.performRequest();
  }

  refreshWorkbasketList() {
    this.pageParameter['page-size'] = this.orientationService.calculateNumberItemsList(
      window.innerHeight,
      92,
      200 + this.toolbarElement.nativeElement.offsetHeight,
      false
    );
    this.performRequest();
  }

  performRequest() {
    this.store.dispatch(new GetWorkbasketsSummary(true, this.filterBy, this.sort, this.pageParameter)).subscribe(() => {
      this.requestInProgressService.setRequestInProgress(false);
    });
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
