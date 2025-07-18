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

import { Component, EventEmitter, inject, Input, OnInit, Output } from '@angular/core';
import { Task } from 'app/workplace/models/task';
import { Workbasket } from 'app/shared/models/workbasket';
import { TaskService } from 'app/workplace/services/task.service';
import { WorkbasketService } from 'app/shared/services/workbasket/workbasket.service';
import { Sorting, TASK_SORT_PARAMETER_NAMING, TaskQuerySortParameter } from 'app/shared/models/sorting';
import { expandDown } from 'app/shared/animations/expand.animation';
import { ActivatedRoute, Router } from '@angular/router';
import { WorkplaceService } from 'app/workplace/services/workplace.service';
import { TaskQueryFilterParameter } from '../../../shared/models/task-query-filter-parameter';
import { Observable, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { KadaiEngineService } from '../../../shared/services/kadai-engine/kadai-engine.service';
import { Actions, ofActionCompleted, Store } from '@ngxs/store';
import { ClearTaskFilter, SetTaskFilter } from '../../../shared/store/filter-store/filter.actions';
import { WorkplaceSelectors } from '../../../shared/store/workplace-store/workplace.selectors';
import { SetFilterExpansion } from '../../../shared/store/workplace-store/workplace.actions';
import { RequestInProgressService } from '../../../shared/services/request-in-progress/request-in-progress.service';
import { MatTab, MatTabGroup } from '@angular/material/tabs';
import { MatButton } from '@angular/material/button';
import { MatTooltip } from '@angular/material/tooltip';
import { AsyncPipe } from '@angular/common';
import { MatIcon } from '@angular/material/icon';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import { MatAutocomplete, MatAutocompleteTrigger } from '@angular/material/autocomplete';
import { MatOption } from '@angular/material/core';
import { TaskFilterComponent } from '../../../shared/components/task-filter/task-filter.component';
import { SortComponent } from '../../../shared/components/sort/sort.component';

export enum Search {
  byWorkbasket = 'workbasket',
  byTypeAndValue = 'type-and-value'
}

@Component({
  selector: 'kadai-task-list-toolbar',
  animations: [expandDown],
  templateUrl: './task-list-toolbar.component.html',
  styleUrls: ['./task-list-toolbar.component.scss'],
  imports: [
    MatTabGroup,
    MatTab,
    MatButton,
    MatTooltip,
    MatIcon,
    MatFormField,
    MatLabel,
    MatInput,
    FormsModule,
    MatAutocompleteTrigger,
    MatAutocomplete,
    MatOption,
    AsyncPipe,
    TaskFilterComponent,
    SortComponent
  ]
})
export class TaskListToolbarComponent implements OnInit {
  @Input() taskDefaultSortBy: TaskQuerySortParameter;
  @Output() performSorting = new EventEmitter<Sorting<TaskQuerySortParameter>>();
  @Output() performFilter = new EventEmitter<TaskQueryFilterParameter>();
  @Output() selectSearchType = new EventEmitter();
  sortingFields: Map<TaskQuerySortParameter, string> = TASK_SORT_PARAMETER_NAMING;
  tasks: Task[] = [];
  workbasketNames: string[] = [];
  filteredWorkbasketNames: string[] = this.workbasketNames;
  resultName = '';
  resultId = '';
  workbaskets: Workbasket[];
  currentBasket: Workbasket;
  workbasketSelected = false;
  searched = false;
  search = Search;
  searchSelected: Search = Search.byWorkbasket;
  activeTab: number = 0;
  filterInput = '';
  isFilterExpanded$: Observable<boolean> = inject(Store).select(WorkplaceSelectors.getFilterExpansion);
  destroy$ = new Subject<void>();
  private kadaiEngineService = inject(KadaiEngineService);
  private taskService = inject(TaskService);
  private workbasketService = inject(WorkbasketService);
  private workplaceService = inject(WorkplaceService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private store = inject(Store);
  private ngxsActions$ = inject(Actions);
  private requestInProgressService = inject(RequestInProgressService);

  ngOnInit() {
    this.ngxsActions$.pipe(ofActionCompleted(ClearTaskFilter), takeUntil(this.destroy$)).subscribe(() => {
      this.filterInput = '';
    });

    this.workbasketService
      .getAllWorkBaskets()
      .pipe(takeUntil(this.destroy$))
      .subscribe((workbaskets) => {
        this.workbaskets = workbaskets.workbaskets;
        this.workbaskets.forEach((workbasket) => {
          this.workbasketNames.push(workbasket.name);
        });

        // get workbasket of current user
        const user = this.kadaiEngineService.currentUserInfo;
        const filteredWorkbasketsByUser = this.workbaskets.filter(
          (workbasket) => workbasket.key == user.userId || workbasket.key == user.userId.toUpperCase()
        );
        if (filteredWorkbasketsByUser.length > 0) {
          const workbasketOfUser = filteredWorkbasketsByUser[0];
          this.resultName = workbasketOfUser.name;
          this.resultId = workbasketOfUser.workbasketId;
          this.workplaceService.selectWorkbasket(workbasketOfUser);
          this.currentBasket = workbasketOfUser;
          this.workbasketSelected = true;
          this.searched = true;
        }
      });

    this.taskService
      .getSelectedTask()
      .pipe(takeUntil(this.destroy$))
      .subscribe((task) => {
        if (typeof task !== 'undefined') {
          const workbasketSummary = task.workbasketSummary;
          if (this.searchSelected === this.search.byWorkbasket && this.resultName !== workbasketSummary.name) {
            this.resultName = workbasketSummary.name;
            this.resultId = workbasketSummary.workbasketId;
            this.currentBasket = workbasketSummary;
            this.workplaceService.selectWorkbasket(this.currentBasket);
            this.workbasketSelected = true;
          }
        }
      });

    this.route.queryParams.subscribe((params) => {
      const component = params.component;
      if (component == 'workbaskets') {
        this.activeTab = 0;
        if (this.currentBasket) {
          this.resultName = this.currentBasket.name;
          this.resultId = this.currentBasket.workbasketId;
        }
        this.selectSearch(this.search.byWorkbasket);
      }
      if (component == 'task-search') {
        this.activeTab = 1;
        this.searched = true;
        this.selectSearch(this.search.byTypeAndValue);
      }
    });

    if (this.router.url.includes('taskdetail')) {
      this.searched = true;
    }
  }

  setFilterExpansion() {
    this.store.dispatch(new SetFilterExpansion());
  }

  onTabChange(search) {
    const tab = search.target.innerText;
    this.requestInProgressService.setRequestInProgress(true);
    if (tab === 'Workbaskets') {
      this.router.navigate(['kadai/workplace'], { queryParams: { component: 'workbaskets' } });
    }
    if (tab === 'Task search') {
      this.router.navigate(['kadai/workplace'], { queryParams: { component: 'task-search' } });
    }
  }

  updateState() {
    const wildcardFilter: TaskQueryFilterParameter = {
      'wildcard-search-value': [this.filterInput]
    };
    this.store.dispatch(new SetTaskFilter(wildcardFilter));
  }

  filterWorkbasketNames() {
    this.filteredWorkbasketNames = this.workbasketNames.filter((value) =>
      value.toLowerCase().includes(this.resultName.toLowerCase())
    );
  }

  searchBasket() {
    this.store.dispatch(new SetFilterExpansion(false));
    this.workbasketSelected = true;
    if (this.searchSelected === this.search.byWorkbasket && this.workbaskets) {
      this.workbaskets.forEach((workbasket) => {
        if (workbasket.name === this.resultName) {
          this.resultId = workbasket.workbasketId;
          this.currentBasket = workbasket;
          this.workplaceService.selectWorkbasket(this.currentBasket);
        }
      });

      this.searched = !!this.currentBasket;

      if (!this.resultId) {
        delete this.currentBasket;
        this.workplaceService.selectWorkbasket();
      }
    }

    this.resultId = '';
  }

  sorting(sort: Sorting<TaskQuerySortParameter>) {
    this.performSorting.emit(sort);
  }

  onFilter() {
    this.performFilter.emit();
  }

  onClearFilter() {
    this.store.dispatch(new ClearTaskFilter()).subscribe(() => {
      this.performFilter.emit();
    });
  }

  createTask() {
    this.taskService.selectTask();
    this.router.navigate([{ outlets: { detail: 'taskdetail/new-task' } }], {
      relativeTo: this.route.parent,
      queryParamsHandling: 'merge'
    });
  }

  selectSearch(type: Search) {
    this.searchSelected = type;
    delete this.resultId;
    this.selectSearchType.emit(type);
    this.searchBasket();
    this.onClearFilter();
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
